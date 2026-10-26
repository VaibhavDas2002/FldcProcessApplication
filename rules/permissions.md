# Permissions Rules — FldcProcessApplication
**Annapurna Yojana — FLDC Process Microservice**
> Audit Date: 2026-10-26 | Auditor: Documentation-only pass
> **DOCUMENTATION/AUDIT ONLY — NO CODE WAS CHANGED**

---

## 1. Role System Overview

| Item | Status | Evidence |
|---|---|---|
| `RoleId.java` class | ✅ Exists — but **completely empty** | `src/main/java/config/RoleId.java` |
| Role constants / enum values | ❌ Not defined anywhere in code | `RoleId.java` has no fields, methods, or constants |
| Role-based access control on endpoints | ❌ Not implemented | `SecurityConfig.java` — all `.permitAll()` |
| Role IDs referenced in business logic | ✅ Hardcoded integers `13` and `15` | `ProcessApplication.java:45` |
| `next_level_role_id` column in DB | ✅ Used as workflow state identifier | `dbt_apy.families`, `dbt_apy.family_members` |

The application has a **role/state concept** (`next_level_role_id`) used to represent the current workflow stage of a family or family member record. However, **no role-to-name mapping** is defined anywhere in the code.

---

## 2. Role IDs Found in Code

These are the **only role ID values** explicitly referenced in the application source:

| Role ID | Context | Meaning | Status | Evidence |
|---|---|---|---|---|
| `13` | `acceptFamily` endpoint — one of two valid state IDs for acceptance | ❓ Unknown (name not defined) | ✅ Referenced | `ProcessApplication.java:45` |
| `15` | `acceptFamily` endpoint — one of two valid state IDs for acceptance | ❓ Unknown (name not defined) | ✅ Referenced | `ProcessApplication.java:45` |

> **IMPORTANT:** The semantic meaning of `next_level_role_id = 13` and `next_level_role_id = 15` is **not documented or named anywhere** in this codebase. `RoleId.java` is a placeholder class with no content.

---

## 3. `next_level_role_id` — Column Semantics

Based on SQL queries found in the code, `next_level_role_id` is used in **both** the `dbt_apy.families` and `dbt_apy.family_members` tables as a **workflow state/stage identifier**, not a user authentication role.

| Table | How Used | Evidence |
|---|---|---|
| `dbt_apy.families` | Filtered with `WHERE next_level_role_id = :roleId` or `IN (:roleIds)` to find families in specific workflow states | `GlobalUpdateCheckHelper.java:152-160` |
| `dbt_apy.family_members` | Filtered with `WHERE next_level_role_id = :roleId` or `IN (:roleIds)` to find members in specific workflow states | `GlobalUpdateCheckHelper.java:249-257` |

This means `next_level_role_id` represents **the next approving role/level in the workflow pipeline**, not a system user's role.

---

## 4. Endpoint Access Permissions (Current State)

The following table documents what permissions are **actually enforced** on each endpoint:

| Endpoint | Method | Authentication Required | Role Required | Evidence |
|---|---|---|---|---|
| `/api/v2/workflow/status` | POST | ❌ None | ❌ None | `SecurityConfig.java:19-20` |
| `/api/v2/workflow/acceptFamily` | POST | ❌ None | ❌ None | `SecurityConfig.java:19-20` |
| `/api/v2/test/test` | GET | ❌ None | ❌ None | `SecurityConfig.java:19-20` |
| `/api/v2/test/jsonInput` | POST | ❌ None | ❌ None | `SecurityConfig.java:19-20` |
| All other paths | ANY | ❌ None | ❌ None | `SecurityConfig.java:20` |

**All endpoints are publicly accessible with no authentication or role check.**

---

## 5. Intended Role-Based Routing (Inferred from DB Schema)

Although not enforced at the HTTP layer, the database schema and query logic imply an approval/verification **workflow** with multiple levels. Based on column names and query patterns:

| Column/Concept | Inferred Meaning | Status |
|---|---|---|
| `next_level_role_id` in `families` / `family_members` | The role ID of the officer/level who must act next in the approval chain | 🔍 Inferred from column name and query context |
| `application_status` | Current status of the application (not acted upon in current endpoints) | 🔍 Inferred from column name; values unknown |
| `approval_datetime` | Timestamp when approval occurred | 🔍 Inferred from column name |
| `verification_datetime` | Timestamp when verification occurred | 🔍 Inferred from column name |
| `created_by_dist_code` | LGD district code of the officer who created the record | 🔍 Inferred from column name and filter usage |
| `created_by_local_body_code` | Local body code of the creating officer (optional filter in helpers) | 🔍 Inferred from column name and filter usage |
| `lgd_block_mc_code` | Block/municipal council LGD code | 🔍 Inferred from column name |
| `lgd_gp_ward_code` | Gram Panchayat / Ward LGD code | 🔍 Inferred from column name |

---

## 6. `GlobalUpdateCheckHelper` — Permission-Check Logic

The `checkingExistingStateFamily` and `checkingExistingStateMembers` methods serve as the primary **state/permission gate** in the application:

### `checkingExistingStateFamily` — Guard Logic
**File:** `src/main/java/util/GlobalUpdateCheckHelper.java:104-172`

The method returns `true` only when ALL of the following conditions are met:

| Condition | Implemented | Details |
|---|---|---|
| `family_id` is non-null and a valid UUID | ✅ Yes | Lines 112-118 |
| `lgd_district_code` is a valid non-null integer | ✅ Yes | Lines 120-123 |
| `next_level_role_id` matches the provided roleId(s), if specified | ✅ Yes | Lines 152-160 |
| `is_deleted = false` | ✅ Yes | Line 140 |
| `lgd_district_code` matches the DB column | ✅ Yes | Line 138 |
| `created_by_local_body_code` matches, if provided | ✅ Yes | Lines 146-149 |

If any condition fails, the method returns `false` without a DB call (fast-fail). DB errors are logged and re-thrown.

### `checkingExistingStateMembers` — Guard Logic
**File:** `src/main/java/util/GlobalUpdateCheckHelper.java:185-269`

| Condition | Implemented | Details |
|---|---|---|
| `member_id` is non-null and a valid UUID | ✅ Yes | Lines 193-198 |
| `family_id` is a valid UUID if provided | ✅ Yes | Lines 201-210 |
| `lgd_district_code` is a valid non-null integer | ✅ Yes | Lines 212-215 |
| `next_level_role_id` matches the provided roleId(s), if specified | ✅ Yes | Lines 249-257 |
| `is_deleted = false` | ✅ Yes | Line 232 |
| `lgd_district_code` matches the DB column | ✅ Yes | Line 230 |
| `created_by_local_body_code` matches, if provided | ✅ Yes | Lines 243-247 |

---

## 7. Zone-Based Data Isolation (District-Level Partitioning)

District code acts as a **data access boundary** — data for a district only exists in its mapped zone's database. This provides implicit data partitioning:

| Rule | Status | Evidence |
|---|---|---|
| All DB queries filter on `lgd_district_code = :distCode` | ✅ Implemented | `familymemberRepository.java:24`, `GlobalUpdateCheckHelper.java:138,230` |
| Zone database is selected based on district code | ✅ Implemented | `ZoneDatabaseManager.resolveZone()` |
| Cross-zone data access is not possible via a single query | ✅ Implemented | Each `NamedParameterJdbcTemplate` is scoped to one zone's data source |

However, this is **data routing**, not **access control**:
- Any caller who knows a valid `lgd_district_code`, `family_id`, and `member_id` can query any district's data.
- There is no validation that the caller is authorized for the supplied district.

---

## 8. `Helper.snapshotFamilyData` — Permission Check Details

**File:** `src/main/java/util/Helper.java:31-99`

This method adds an **additional filter** not present in `GlobalUpdateCheckHelper`:

```sql
AND created_by_dist_code = :distCode
```

This means for the snapshot, records are only returned if:
- `lgd_district_code = :distCode` AND
- `created_by_dist_code = :distCode`

This is a **stricter filter** — requiring the record to have been both located in and created by the same district. This is not applied in the main `status` endpoint (which uses `familymemberRepository.findByDistrictCode`).

---

## 9. Contradictions and Inconsistencies

| Contradiction | Details |
|---|---|
| `spring-boot-starter-security` is declared, but all endpoints are `permitAll()` | Security library is included but provides no actual protection |
| Spring Session JDBC is configured (stateful) but CSRF is disabled | CSRF protection is typically recommended with stateful sessions |
| `redis` package is in `scanBasePackages` and `spring-boot-starter-data-redis-reactive` is in `pom.xml`, but no Redis beans or configuration exist | Redis dependency is unused |
| `RoleId.java` class exists in the `config` package but is empty | Placeholder with no constants, suggesting role naming was planned but not implemented |
| `security` package is in `scanBasePackages` but does not exist on disk | A separate security package is planned/was planned but not yet created |
| `middleware` package is in `scanBasePackages` but does not exist | Middleware (request interceptors, auth filters) was planned but not created |
| `created_by_dist_code = :distCode` filter in `Helper.snapshotFamilyData` but absent from `familymemberRepository.findByDistrictCode` | Inconsistent strictness of district-boundary enforcement between helper methods |
| `TestController` is a permanent Spring bean with no profile guard | Development/debug endpoints are always active |

---

## 10. Permissions Summary

| Category | Status | What Exists | What Is Missing |
|---|---|---|---|
| HTTP Authentication | ❌ Not enforced | Spring Security declared | Auth rules, credentials, user store |
| HTTP Authorization (RBAC on routes) | ❌ Not enforced | None | `.hasRole()` / `.hasAuthority()` rules |
| Workflow state-based permission checks | ✅ Implemented | `checkingExistingStateFamily` / `checkingExistingStateMembers` | Role ID semantic names |
| District-based data isolation | ✅ Partially enforced | `lgd_district_code` filter on all queries | Caller-identity validation against district |
| Soft-delete record filtering | ✅ Enforced | `is_deleted = false` on all queries | — |
| Role definitions / constants | ❌ Not implemented | `RoleId.java` (empty) | Named constants for role IDs |
| Local body code access control | ✅ Implemented (optional filter) | `created_by_local_body_code` filter in helpers | Made mandatory where required |
| Creator district enforcement | ✅ In `Helper.snapshotFamilyData` only | `created_by_dist_code` filter | Consistent application across all queries |
