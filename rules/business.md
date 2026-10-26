# Business Rules — FldcProcessApplication
**Annapurna Yojana — FLDC Process Microservice**
> Audit Date: 2026-10-26 | Auditor: Documentation-only pass
> **DOCUMENTATION/AUDIT ONLY — NO CODE WAS CHANGED**

---

## 1. Application Context

| Property | Value | Status | Evidence |
|---|---|---|---|
| Application name | `FldcProcessApplication` | ✅ Implemented | `application.properties:1` |
| Group / Artifact | `com.nic` / `FldcProcessApplication` | ✅ Implemented | `pom.xml:11-12` |
| Server port | `8081` | ✅ Implemented | `application.properties:2` |
| Framework | Spring Boot 4.1.1 / Java 21 | ✅ Implemented | `pom.xml:8,30` |
| Domain | Annapurna Yojana – family/member FLDC processing | ✅ Inferred | Package names, DB schema `dbt_apy`, code comments |

This is a **backend-only REST microservice**. No frontend, no UI, no template engine. All responses are JSON.

---

## 2. API Endpoints (Actual Routes)

All routes are prefixed `/api/v2/`.

### 2.1 `/api/v2/workflow` — Core Process Workflow

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/api/v2/workflow/status` | Fetch FLDC status for a family member by district code | ✅ Implemented |
| `POST` | `/api/v2/workflow/acceptFamily` | Check whether a family exists in allowed states for acceptance | ✅ Implemented |

**Evidence:** `src/main/java/processWorkflow/ProcessApplication.java`

#### `POST /api/v2/workflow/status`
- **Parameters (query):** `districtCode` (int, required), `family_id` (String, required), `member_id` (String, required)
- **Returns:** `List<Map<String, Object>>` — raw DB rows from `dbt_apy.family_members`
- **Columns returned:** `next_level_role_id`, `approval_datetime`, `verification_datetime`
- **Routing:** Uses `districtCode` to select the correct zone database (Zone 1 or Zone 2)

#### `POST /api/v2/workflow/acceptFamily`
- **Body (JSON):** `{ "lgd_district_code": int, "family_id": UUID-string }`
- **Business rule enforced:** Checks that the family has a `next_level_role_id` of **13 or 15**
  - Role IDs `[13, 15]` are **hardcoded** as the accepted state values
  - Returns `{ "exists": true/false }` indicating whether the family is in a valid state for acceptance
- **Validation:** `family_id` must be a valid UUID (36-char format); non-UUID values return `false`
- **Validation:** `lgd_district_code` must be a non-null, parseable integer; null district returns `false`
- **Evidence:** `ProcessApplication.java:43-47`, `GlobalUpdateCheckHelper.java:104-172`

> **IMPORTANT:** Role IDs `13` and `15` are hardcoded inline in `ProcessApplication.java` at line 45 (`List.of(13, 15)`). No constant, enum, or configuration key names them. `RoleId.java` exists as an empty placeholder class with no defined constants.

---

### 2.2 `/api/v2/test` — Development / Debugging Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| `GET` | `/api/v2/test/test` | Returns a static success response | ✅ Implemented |
| `POST` | `/api/v2/test/jsonInput` | Echoes `lgd_district_code` and `family_id` from request body | ✅ Implemented |

**Evidence:** `src/main/java/processWorkflow/TestController.java`

> **WARNING:** `TestController` is mapped under `/api/v2/test` and is currently **fully accessible** (no auth/role guard). It appears to be a development utility. There is no `@Profile("dev")` or conditional annotation limiting it to non-production environments.

---

## 3. Database Schema (Accessed Tables)

Database name: `annapurna_consolidated` (env override) or zone-specific (`annapurna_z1`, `annapurna_z2`). Schema: `dbt_apy`.

| Table | Columns Accessed | Access Type | Evidence |
|---|---|---|---|
| `dbt_apy.families` | `id`, `tmp_application_id`, `app_serial`, `created_by_dist_code`, `created_by_local_body_code`, `next_level_role_id`, `application_status`, `lgd_district_code`, `create_mode`, `is_deleted` | Read | `GlobalUpdateCheckHelper.java:136-162`, `Helper.java:47-57` |
| `dbt_apy.family_members` | `id`, `family_id`, `family_serial`, `next_level_role_id`, `application_status`, `created_by_dist_code`, `lgd_district_code`, `lgd_block_mc_code`, `lgd_gp_ward_code`, `create_mode`, `verification_datetime`, `approval_datetime`, `is_deleted`, `created_by_local_body_code` | Read | `familymemberRepository.java:24`, `GlobalUpdateCheckHelper.java:227-259`, `Helper.java:73-83` |

**All database access in this service is read-only.** No `INSERT`, `UPDATE`, or `DELETE` SQL is present in any source file.

---

## 4. Zone-Based District Routing

This is a **core business rule** of the application.

### 4.1 Zone Definition

Districts are assigned to named zones (`zone1`, `zone2`). Each zone has its own PostgreSQL read/write database instances. The zone is resolved from the **LGD district code** in the request.

| Zone | Default District Codes | Default DB Name |
|---|---|---|
| zone1 | `306, 304, 664, 305, 307, 308, 309, 310, 311, 312, 313, 314` | `annapurna_z1` / `annapurna_consolidated` |
| zone2 | `303, 703, 702, 315, 316, 317, 318, 319, 320, 704, 306, 321` | `annapurna_z2` / `annapurna_consolidated` |

> **NOTE:** District code `306` appears in **both Zone 1 and Zone 2** default district lists (`application.properties:10-11`). In the `ConcurrentHashMap`, the last write wins — zone2 overwrites zone1 for code 306 if zone2 is populated after zone1 during `@PostConstruct`. This is a **potential routing ambiguity**.

**Evidence:** `application.properties:10-11`, `ZoneDatabaseManager.java:112-135`

### 4.2 Zone Resolution Rules (Implemented)
1. If `districtCode` is passed explicitly in the request, it is used directly.
2. If no district code is passed, session attributes `lgd_session.district_id` or `district_id` are checked.
3. If no session attribute exists, the **default zone** (`zone1`) is used as fallback.
4. If the resolved zone number exceeds `active-zones`, it falls back to the default zone.

**Evidence:** `ZoneDatabaseManager.java:140-188`

### 4.3 Connection Pool Settings (Implemented)

Each of the 6 data sources uses HikariCP with these settings:

| Setting | Value |
|---|---|
| Maximum pool size | 10 |
| Minimum idle | 2 |
| Connection timeout | 30,000 ms |
| Idle timeout | 600,000 ms (10 min) |
| Max lifetime | 1,800,000 ms (30 min) |

**Evidence:** `ZoneDatabaseConfig.java:89-100`

---

## 5. Data Validation Rules (Implemented)

| Field | Rule | Where Enforced |
|---|---|---|
| `family_id` | Must be a valid UUID (exactly 36 chars, `UUID.fromString` parseable) | `GlobalUpdateCheckHelper.java:116, 417-431` |
| `member_id` | Must be a valid UUID when provided for member-level checks | `GlobalUpdateCheckHelper.java:197` |
| `lgd_district_code` | Must be a non-null, parseable integer | `GlobalUpdateCheckHelper.java:120-123` |
| `roleId` | Accepts Integer, Collection, int[], long[], Iterable, or String; empty collection → returns `false` | `GlobalUpdateCheckHelper.java:125-131, 355-398` |
| `localBodyCode` | Optional; if provided, must be integer-castable or a non-empty string | `GlobalUpdateCheckHelper.java:335-353` |
| `is_deleted` | All queries filter `is_deleted = false`; soft-deleted records are never returned | Multiple queries across `GlobalUpdateCheckHelper.java`, `Helper.java` |

---

## 6. Standard API Response Format (Implemented)

All API responses follow a uniform JSON envelope:

```json
{
  "status": true,
  "statusCode": 200,
  "message": "...",
  "data": { ... }
}
```

- `status`: `true` for success, `false` for error
- `statusCode`: HTTP status integer
- `message`: human-readable string
- `data`: payload or `null`

**Evidence:** `src/main/java/responseHandler/ResponseHandler.java`

Exceptions caught in `acceptFamily` return HTTP `400 Bad Request` with `status: false`.

---

## 7. JSON Field Alias (Implemented)

The `GetFamilyInput` DTO accepts either `lgd_distrct_code` (typo) **or** `lgd_district_code` as a JSON key — both map to the same field.

**Evidence:** `GetFamilyInput.java:7` — `@JsonAlias({"lgd_distrct_code", "lgd_district_code"})`

> **NOTE:** This is a backward-compatibility alias for a misspelled field name.

---

## 8. Declared Scan Packages (Some Empty/Missing)

The `@SpringBootApplication` explicitly scans:

| Package | Status |
|---|---|
| `com.nic.FldcProcessApplication` | ✅ Has files |
| `config` | ✅ Has files |
| `processWorkflow` | ✅ Has files |
| `repository` | ✅ Has files |
| `resDTO` | ✅ Has files |
| `responseHandler` | ✅ Has files |
| `service` | ✅ Has files |
| `util` | ✅ Has files |
| `exception` | ⚠️ Missing — no directory or files found |
| `log` | ⚠️ Missing — no directory or files found |
| `middleware` | ⚠️ Missing — no directory or files found |
| `redis` | ⚠️ Missing — no directory or files found |
| `reqDTO` | ⚠️ Missing — no directory or files found |
| `security` | ⚠️ Missing — no directory or files found |

**Evidence:** `com/nic/FldcProcessApplication/FldcProcessApplication.java:6-21`

> **WARNING:** 6 packages are declared in `scanBasePackages` but do **not exist** in the project. This indicates planned but unimplemented features (Redis caching, middleware, request DTOs, dedicated security package, exception handlers, structured logging).

---

## 9. Missing or Unknown Business Rules

| Item | Status | Notes |
|---|---|---|
| What `next_level_role_id` values represent semantically (e.g., role 13 = "Block Officer acceptance") | ❓ Unknown | `RoleId.java` is empty; no enum, constant, or comment defines role meanings |
| Complete list of valid district codes per zone in production | ❓ Unknown | Only default/dev codes are in `application.properties`; production codes come from env vars |
| Definition of `create_mode` values in `families`/`family_members` | ❓ Unknown | Column is read but never filtered in current code |
| Definition of `application_status` values | ❓ Unknown | Column is read but never filtered or acted upon in current endpoints |
| Meaning of `tmp_application_id`, `app_serial`, `family_serial` | ❓ Unknown | Read in snapshots but not filtered or acted upon |
| Whether Zone 3+ are planned | 🔍 Inferred | `activeZones` property and zone guard logic exist; zone3 is commented out in `application.properties:12` |
