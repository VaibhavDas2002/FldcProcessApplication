# FLDC / Annapurna Yojana (APY) Workflow Module

A production-grade government e-governance workflow engine migrating the legacy Laravel/Livewire implementation to Spring Boot 3 / Java 21, with strict auditability, state-machine transitions, multi-tenant zone database routing, and JWT-authenticated role-based access control.

---

## 1. Overview & Architecture

The FLDC (Family Level Data Collection) / Annapurna Yojana (APY) portal coordinates multi-tier administrative processing across five key roles:
1. **Citizen**: Direct online draft submission and correction resubmission.
2. **Operator (CSC / Office)**: Offline/office draft entry and correction resubmission.
3. **Enquiry Officer**: Field/desk enquiry, recommendation for accept/reject, or revert to Citizen/Operator.
4. **Verifier**: Tab-based queue reviews, family acceptance/rejection/revert/hold, and single/batch verification of eligible female beneficiaries (with mixed-revert cascade enforcement).
5. **Approver**: Final individual/bulk approval, single/family hold, rejection, or mixed-revert cascade to Citizen/Operator.
6. **Administrator / RevertRejectToApprove**: Administrative post-rejection re-processing (re-approval or re-opening for enquiry).

### Architectural Highlights
- **Layered, Package-by-Feature Design**: `controller` -> `service` -> `repository` -> `entity` with strict DTO boundaries. JPA entities are never leaked through the API.
- **Explicit Deterministic State Machine**: Modelled after the legacy transition tables, validating `(Role, Current next_level_role_id, Target Action)`. Invalid transitions fail fast with `InvalidWorkflowTransitionException`.
- **Atomic Transactional Invariants**: Every workflow action mutates family/member columns and records an immutable audit trail entry in `dbt_apy.accept_reject_info` in a single `@Transactional` unit.
- **Multi-Tenant / ZoneDB Routing**: Supports dynamic database routing (`ZoneRoutingDataSource` extending `AbstractRoutingDataSource` and `ZoneDB` facade) across partitioned district clusters.
- **Pure Resource Server**: No internal authentication microservice. Validates JWT Bearer tokens issued by the central identity authority containing user roles, district code, and office metadata.

---

## 2. Technology Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.x / 4.x, Spring Web, Spring Data JPA (Hibernate), Spring Security (JWT)
- **Database**: PostgreSQL 15+ (Schemas: `dbt_apy`, `cross_check`)
- **Database Migrations**: Flyway (`V1__init_workflow_tables.sql`, `V2__create_indexes.sql`)
- **DTO Mapping**: MapStruct 1.5.5.Final with Lombok binding
- **Documentation**: springdoc-openapi 2.6.0 (Swagger UI)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

---

## 3. Project Structure

```
src/main/java/com/nic
├── FldcProcessApplication.java           # Spring Boot bootstrap & scanning configuration
├── fldc
│   ├── common
│   │   ├── api                           # Standard ApiResponse & PageResponse
│   │   ├── config                        # SecurityConfig, CorsConfig, OpenApiConfig, JpaAuditConfig
│   │   ├── exception                     # GlobalExceptionHandler, domain exceptions, ErrorCode
│   │   ├── logging                       # RequestMdcFilter (MDC tracking with X-Request-Id)
│   │   ├── routing                       # ZoneRoutingDataSource, ZoneContextHolder
│   │   └── security                      # JwtTokenProvider, JwtAuthenticationFilter, UserPrincipal
│   ├── workflow
│   │   ├── controller                    # Citizen, Operator, Enquiry, Verifier, Approver, Reprocess
│   │   ├── dto                           # Request & Response DTOs
│   │   ├── entity                        # Family, FamilyMember, AcceptRejectInfo, LbDataPaidApy
│   │   ├── mapper                        # MapStruct mappers (FamilyMapper, FamilyMemberMapper)
│   │   ├── repository                    # Spring Data JPA repositories & specifications
│   │   │   └── specification             # Tab-based lazy queue specifications
│   │   ├── service                       # WorkflowExecutionService, QueueQueryService, AuditTrailService
│   │   └── statemachine                  # NextLevelRoleId, ApplicationStatus, OpType, WorkflowStateMachine
│   └── lot                               # Batch payment workflow (cross_check schema)
│       ├── controller                    # LotController
│       ├── entity                        # ApyLotMaster, ApyLotDetails
│       ├── repository                    # ApyLotMasterRepository, ApyLotDetailsRepository
│       └── service                       # LotService
```

*(Legacy packages `util.ZoneDB`, `util.GlobalUpdateCheckHelper`, and `/api/v2/workflow` controllers are preserved for 100% backward compatibility)*.

---

## 4. Master Reference: Next Level Role IDs

| Role ID | Status Name / Description | Target Scope | Triggered By |
| :---: | :--- | :---: | :--- |
| **0** | Draft Entry | Both (`f`, `fm`) | Citizen / Operator draft |
| **4** | Revert Application (Citizen Entry) - Approver | Both | Approver Stage (reverts whole family to Citizen) |
| **5** | Revert Application (Citizen Entry) - Enquiry | Both | Enquiry Stage (reverts whole family to Citizen) |
| **6** | Revert Application (Citizen Entry) - Verifier | Both | Verifier Stage (reverts whole family to Citizen) |
| **7** | Revert Application (Office Entry) - Enquiry | Both | Enquiry Stage (reverts whole family to Operator) |
| **8** | Revert Application (Office Entry) - Verifier | Both | Verifier Stage (reverts whole family to Operator) |
| **9** | Revert Application (Office Entry) - Approver | Both | Approver Stage (reverts whole family to Operator) |
| **10** | Final Submission | Both | Citizen / Operator initial submission |
| **11** | Recommend for Enquiry - Verifier | Both | Verifier Stage (routes back to Enquiry Officer) |
| **13** | Recommend For Rejection - Enquiry Officer | Both | Enquiry Officer (forwards to Verifier as rejected) |
| **15** | Recommend For Accept - Enquiry Officer | Both | Enquiry Officer (forwards to Verifier as accepted) |
| **17** | Verifier Hold / Further Verification Required | Both | Verifier Stage (verifier puts family on hold) |
| **18** | Accept Application / Family Details | Both | Verifier Stage (family accepted; enables member action) |
| **20** | Verify Member (Annapurna) Accepted Application | Member | Verifier Stage (member verified; forwards to Approver) |
| **25** | Hold Member / Family Details - Approver | Member | Approver Stage (temp hold on approved member) |
| **26** | Approver Hold / Further Verification Required | Both / Member| Approver Stage (approver puts family or member on hold)|
| **27** | Recommend Approved Member For Rejection | Member | Verifier Stage (post-approval exclusion recommendation)|
| **30** | Approve Member (Annapurna) - Approver | Member | Approver Stage (final approval) |
| **-100** | Reject Application / Family | Both | Verifier Stage (whole family rejected) |
| **-101** | Reject Accepted Member (Annapurna) | Member | Verifier Stage (individual member rejected) |
| **-102** | Reject Member (Annapurna) | Member | Approver Stage (approver rejects verified member) |
| **-103** | Reject Approved / Recommended Member | Member | Approver Stage (rejects member from role 27) |
| **-104** | Reject Hold Member | Member | Approver Stage (rejects member from role 25) |
| **-106** | Reject Priority Re-verification | Member | Priority Process Stage |

---

## 5. Critical Business Rules Implemented

1. **Member Eligibility Criteria**:
   - `applying_for_annapurna_bhandar = true`
   - `gender = 'F'`
   - Age between 25 and 60 years
   - `next_level_role_id = 18` (family must be accepted before member verification)
2. **Mixed-Revert Cascade**:
   - Reverting a single member in a batch or at Approver level cascades revert status to the **entire family and all non-approved members**:
     * Office entry (`create_mode = 'O'`): `f.next_level_role_id = 8` (Verifier) or `9` (Approver), `detail_status = 99`, `application_status = 'REVERTED'`
     * Citizen entry (`create_mode = 'C'`): `f.next_level_role_id = 6` (Verifier) or `4` (Approver), `detail_status = 99`, `application_status = 'REVERTED'`
   - Returns client warning: `"You Cannot Revert a single application, on Confirmation whole application has been reverted"`.
3. **Immutability of Approved Beneficiaries (Role 30)**:
   - Any family-level action (Accept, Reject, Revert, Hold, Recommend for Enquiry) strictly enforces `next_level_role_id <> 30`. Approved beneficiaries are never overwritten by family mutations.
4. **Remarks Length Validation**:
   - Recommend for Enquiry: Mandatory, minimum **5 characters**.
   - Verifier / Approver Hold: Mandatory, minimum **15 characters**.
   - Lot Rejection: Mandatory, minimum **3 characters**.
5. **Immutable Audit Trail**:
   - Every single workflow transaction logs `(family_id, family_member_id, lgd_district_code, op_type, reject_revert_cause_id, remarks, user_id, ip_address, created_at)` to `dbt_apy.accept_reject_info`.

---

## 6. Environment Configuration

### Setup `.env`
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

Configure local environment variables:
```dotenv
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=INFO

DB_HOST=localhost
DB_PORT=5432
DB_NAME=annapurna_consolidated
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_SCHEMA=dbt_apy
DB_POOL_SIZE=10

JWT_SECRET=fldc_annapurna_yojana_dev_jwt_secret_key_minimum_32_bytes_long!
JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080

ZONE_DB_ACTIVE_ZONES=2
ZONE_DB_DEFAULT=zone1
ZONE1_DISTRICT_CODES=306,304,664,305,307,308,309,310,311,312,313,314
ZONE2_DISTRICT_CODES=303,703,702,315,316,317,318,319,320,704,306,321
```

`.env` is automatically imported by Spring Boot via `spring.config.import=optional:file:.env[.properties]`.

---

## 7. How to Build & Run

### Prerequisites
- JDK 21
- PostgreSQL 15+ running on port 5432
- Maven 3.9+ (or included `./mvnw`)

### Compile & Run Tests
```bash
# Run all unit and integration tests
./mvnw clean test
```

### Start the Application
```bash
# Start locally with dev profile
./mvnw spring-boot:run
```
The server starts on port `8081`.

### Access OpenAPI / Swagger UI
Open your browser at:
`http://localhost:8081/swagger-ui.html`

---

## 8. Role-Wise API Endpoints

### 8.1 Citizen & Operator
- `POST /api/v1/workflow/citizen/submit` — Submit fresh citizen draft application
- `POST /api/v1/workflow/citizen/resubmit` — Resubmit reverted citizen application
- `POST /api/v1/workflow/operator/submit` — Submit fresh office entry application
- `POST /api/v1/workflow/operator/resubmit` — Resubmit reverted office application

### 8.2 Enquiry Officer
- `POST /api/v1/workflow/enquiry/queue` — Tab-based lazy queue (Tab 1: Under Enquiry, 2: Office, 3: Online, 4: APY Paid, 5: Recommended from Verifier)
- `GET /api/v1/workflow/enquiry/{familyId}?districtCode=304` — View application details
- `POST /api/v1/workflow/enquiry/recommend-accept` — Forward to Verifier as accepted (`role 15`, `ENQUIRY_PROCESSED`)
- `POST /api/v1/workflow/enquiry/recommend-reject` — Forward to Verifier as rejected (`role 13`, `ENQUIRY_REJECTED`)
- `POST /api/v1/workflow/enquiry/revert` — Revert to Operator (`role 7`) or Citizen (`role 5`)
- `POST /api/v1/workflow/enquiry/edit-save` — Update enquiry details and forward to Verifier

### 8.3 Verifier
- `POST /api/v1/workflow/verifier/queue` — Tab-based queue (Tab 1: Under Enquiry Processed, 2: Pending after Enquiry, 3: Accepted, 4-9: Paid/JKS/Physical/July/Claim/ASAP)
- `GET /api/v1/workflow/verifier/{familyId}?districtCode=304` — View family details
- `GET /api/v1/workflow/verifier/{familyId}/eligible-members?districtCode=304` — List eligible members for verification (Female, 25-60, AY=true, Role=18)
- `POST /api/v1/workflow/verifier/accept-family` — Accept family details (`f & fm role 18`, `ACCEPTED`)
- `POST /api/v1/workflow/verifier/reject-family` — Reject family application (`role -100`, `REJECTED`)
- `POST /api/v1/workflow/verifier/revert-family` — Revert family (`role 8` Office / `6` Citizen, `REVERTED`, `detail_status = 99`)
- `POST /api/v1/workflow/verifier/recommend-enquiry` — Send back to Enquiry (`role 11`, `SENT_FOR_ENQUIRY`, min 5 chars remarks)
- `POST /api/v1/workflow/verifier/hold-family` — Further verification required (`role 17`, `HELDUP_V`, min 15 chars remarks)
- `POST /api/v1/workflow/verifier/confirm-verification` — Single or batch verification; **mixed-revert triggers full cascade**
- `POST /api/v1/workflow/verifier/recommend-reject-approved` — Flag approved member for exclusion (`role 30 -> 27`)

### 8.4 Approver
- `POST /api/v1/workflow/approver/queue` — List verified members (`role 20`, `applying_for_annapurna_bhandar = true`)
- `POST /api/v1/workflow/approver/approve-member/{memberId}?districtCode=304` — Single member approval (`role 30`, `APPROVED`)
- `POST /api/v1/workflow/approver/bulk-approve` — Bulk member approval (`role 20 -> 30`)
- `POST /api/v1/workflow/approver/reject-member/{memberId}` — Member rejection (`role -102`, `REJECTED`)
- `POST /api/v1/workflow/approver/revert-member/{memberId}` — Revert member with **whole family cascade** (`role 9` Office / `4` Citizen, `detail_status = 99`)
- `POST /api/v1/workflow/approver/hold-family` — Approver hold whole family (`role 26`, `HELDUP_A`, min 15 chars)
- `POST /api/v1/workflow/approver/hold-member` — Approver hold single member (`role 26`, `HELDUP_M_A`, min 15 chars)
- `POST /api/v1/workflow/approver/temp-hold` — Temporary hold on approved member (`role 25`, `HOLD_<type>`)
- `POST /api/v1/workflow/approver/reject-hold-member/{memberId}` — Reject held member (`role -104`)
- `POST /api/v1/workflow/approver/reject-recommended/{memberId}` — Reject recommended member (`role -103`)

### 8.5 Post-Rejection Re-Processing (`RevertRejectToApprove`)
- `POST /api/v1/workflow/reprocess/queue` — List records in `next_level_role_id in (-100, -101, -102, -103)`
- `POST /api/v1/workflow/reprocess/re-approve/{memberId}?districtCode=304` — Re-approve from rejected (`role 30`, `APPROVE`)
- `POST /api/v1/workflow/reprocess/send-to-enquiry/{familyId}?districtCode=304&remarks=...` — Send back to enquiry (`role 10`, `REJECT TO ENQUIRY`)

### 8.6 Batch Payment Lots (`cross_check` schema)
- `GET /api/v1/workflow/lots/pending?districtCode=304` — View pending lots (`lot_status = 0`)
- `POST /api/v1/workflow/lots/{lotId}/approve` — Approve lot master and detail records (`lot_status = 2`)
- `POST /api/v1/workflow/lots/{lotId}/reject?remarks=...` — Reject lot (`lot_status = -3`)
- `POST /api/v1/workflow/lots/details/{detailId}/toggle-reject` — Toggle rejection of member in lot

---

## 9. Sample Requests & Curl Examples

### 1. Authenticate with Bearer Token
Include your JWT token in the `Authorization` header:
```bash
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 2. Verifier: Accept Family
```bash
curl -X POST http://localhost:8081/api/v1/workflow/verifier/accept-family \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "familyId": "550e8400-e29b-41d4-a716-446655440000",
    "districtCode": 304,
    "remarks": "Income and resident certificates verified."
  }'
```

### 3. Verifier: Confirm Verification with Mixed-Revert Cascade
```bash
curl -X POST http://localhost:8081/api/v1/workflow/verifier/confirm-verification \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "familyId": "550e8400-e29b-41d4-a716-446655440000",
    "districtCode": 304,
    "members": [
      {
        "memberId": "660e8400-e29b-41d4-a716-446655440001",
        "action": "VERIFY",
        "notInAsdd": false,
        "appealTribunal": "Tribunal #12"
      },
      {
        "memberId": "660e8400-e29b-41d4-a716-446655440002",
        "action": "REVERT",
        "rejectRevertCauseId": 7,
        "remarks": "Bank account number invalid"
      }
    ]
  }'
```
*Result*: Detects mixed revert, cascades revert to whole family (role 8 or 6, `detail_status = 99`, `application_status = 'REVERTED'`), logs `REVERT_FAMILY` audit trail, and returns:
`"You Cannot Revert a single application, on Confirmation whole application has been reverted."`

### 4. Approver: Bulk Approve Members
```bash
curl -X POST http://localhost:8081/api/v1/workflow/approver/bulk-approve \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "districtCode": 304,
    "memberIds": [
      "660e8400-e29b-41d4-a716-446655440001",
      "660e8400-e29b-41d4-a716-446655440003"
    ]
  }'
```

---

## 10. Test Verification Results

All 79 unit and integration tests execute cleanly:
```
[INFO] Running com.nic.fldc.queue.QueueSpecificationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nic.fldc.statemachine.WorkflowStateMachineTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nic.fldc.workflow.EligibilityValidationTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nic.fldc.workflow.MixedRevertCascadeTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nic.fldc.workflow.WorkflowExecutionServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nic.FldcProcessApplication.FldcProcessApplicationTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running processWorkflow.ProcessApplicationTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running service.ProcessApplicationServiceTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running util.GlobalUpdateCheckHelperTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running util.HelperTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 79, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
