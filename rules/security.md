# Security Rules — FldcProcessApplication
**Annapurna Yojana — FLDC Process Microservice**
> Audit Date: 2026-10-26 | Auditor: Documentation-only pass
> **DOCUMENTATION/AUDIT ONLY — NO CODE WAS CHANGED**

---

## 1. Security Framework

| Property | Value | Status | Evidence |
|---|---|---|---|
| Security library | Spring Security (via `spring-boot-starter-security`) | ✅ Implemented | `pom.xml:38-40` |
| Session management | Spring Session JDBC (`spring-boot-starter-session-jdbc`) | ✅ Implemented | `pom.xml:42-44` |
| Redis reactive starter | Present as a dependency | ✅ Declared (unused in current code) | `pom.xml:34-36` |
| Session store initialization | `spring.session.jdbc.initialize-schema=always` | ✅ Implemented | `application.properties:66` |
| CSRF protection | **Disabled** | ⚠️ Implemented (intentional) | `SecurityConfig.java:17` |

---

## 2. HTTP Security Configuration

**File:** `src/main/java/config/SecurityConfig.java`

```java
http
    .csrf(AbstractHttpConfigurer::disable)
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/**").permitAll()
        .anyRequest().permitAll()
    );
```

### 2.1 Findings

| Rule | Value | Status | Risk |
|---|---|---|---|
| CSRF disabled | `.csrf(AbstractHttpConfigurer::disable)` | ✅ Implemented | Medium — intentional, acceptable for stateless/internal REST APIs; session is JDBC-backed which is inconsistent with CSRF disabled |
| All `/api/**` requests | `.permitAll()` — no authentication required | ✅ Implemented | High — no auth check on any API endpoint |
| All other requests | `.permitAll()` — no authentication required | ✅ Implemented | High — catch-all permits everything |
| Authentication enforcement | **None** | ⚠️ Missing | All requests allowed without credentials |
| Role-based access control | **None configured in SecurityConfig** | ⚠️ Missing | No `.hasRole()` or `.hasAuthority()` on any route |
| HTTPS enforcement | **Not configured** | ❓ Unknown | No `requiresSecure()` or redirect rule in the code |

> **CRITICAL FINDING:** The Spring Security configuration effectively **disables all authentication and authorization** for every endpoint. Both the `/api/**` matcher and the `.anyRequest()` catch-all call `.permitAll()`. This means **any caller can invoke any endpoint without credentials**.

---

## 3. Session Management

| Property | Value | Status | Evidence |
|---|---|---|---|
| Session store backend | PostgreSQL (JDBC) | ✅ Implemented | `pom.xml:42-44`, `application.properties:66` |
| Session schema auto-init | `always` (creates Spring Session tables on startup) | ✅ Implemented | `application.properties:66` |
| Session attribute used for routing | `lgd_session.district_id` or `district_id` | ✅ Implemented | `ZoneDatabaseManager.java:140-156` |
| Session attribute scope | `SCOPE_SESSION` | ✅ Implemented | `ZoneDatabaseManager.java:144,146` |
| Redis session store | Declared dependency but **no Redis session config found** | ⚠️ Inconsistent | `pom.xml:34-36`; no `@EnableRedisHttpSession` or Redis session bean found |

> **NOTE:** The session reads `lgd_session.district_id` first, then falls back to `district_id`. This session value drives which zone database is queried. There is no validation that the session district code matches the request district code — a caller could supply a different `districtCode` parameter than what is in their session.

---

## 4. CSRF Protection

**Status: Disabled (Implemented)**

CSRF is explicitly disabled in `SecurityConfig.java:17`. This is a common pattern for REST APIs that rely on stateless token-based auth, but this service uses **JDBC-backed sessions**, which are stateful. The combination of stateful sessions and disabled CSRF is a known security consideration.

**Evidence:** `src/main/java/config/SecurityConfig.java:17`

---

## 5. SQL Injection Prevention

| Mechanism | Status | Evidence |
|---|---|---|
| Parameterized queries via `NamedParameterJdbcTemplate` | ✅ Implemented | All SQL in `GlobalUpdateCheckHelper.java`, `Helper.java`, `familymemberRepository.java` uses named parameter binding |
| Raw string concatenation in SQL | ✅ Not present | All user-supplied values go through `:paramName` binding |
| `CAST(:family_id AS uuid)` in SQL | ✅ Implemented | Adds DB-level UUID type enforcement even after Java-level UUID validation |

All database queries use Spring's `NamedParameterJdbcTemplate` with named parameters (`params.put(...)`) — no direct string interpolation of user input into SQL.

---

## 6. Input Validation as a Security Control

| Control | Status | Evidence |
|---|---|---|
| UUID format validation before DB query | ✅ Implemented | `GlobalUpdateCheckHelper.isValidUuid()` — checks 36-char format via `UUID.fromString()` |
| District code must be a parseable integer | ✅ Implemented | `GlobalUpdateCheckHelper.normalizeDistrictCode()` |
| Early-return on invalid inputs (before DB hit) | ✅ Implemented | Multiple `if (family_id == null) return false;` guards |
| `is_deleted = false` filter on all queries | ✅ Implemented | Prevents access to soft-deleted records |

**Evidence:** `src/main/java/util/GlobalUpdateCheckHelper.java:112-131`

---

## 7. Error Handling and Information Disclosure

| Control | Status | Evidence |
|---|---|---|
| Exception caught in `acceptFamily` controller | ✅ Implemented | `ProcessApplication.java:50-52` |
| Error response uses `e.getMessage()` in the response body | ⚠️ Caution | `ProcessApplication.java:51` — exposes raw exception message to the caller |
| DB errors re-thrown in `GlobalUpdateCheckHelper` | ⚠️ Implemented | `GlobalUpdateCheckHelper.java:167-170` — `DataAccessException` is caught, logged, and re-thrown; caller sees it |
| Stack traces in responses | ❓ Unknown | Depends on Spring Boot's default error handling config; no custom `@ControllerAdvice` or `ErrorController` found |

> **CAUTION:** `e.getMessage()` from a `DataAccessException` or any other exception is returned directly to the client in `acceptFamily`. This may expose internal DB structure, table names, or SQL errors to the caller.

---

## 8. Credential and Secret Management

| Property | Status | Evidence |
|---|---|---|
| DB credentials externalized via env vars | ✅ Implemented | `application.properties` uses `${ENV_VAR:default}` pattern throughout |
| Default credentials hardcoded as fallback | ⚠️ Caution | Default username/password is `postgres` / `postgres` for all 6 data sources |
| Default DB host hardcoded in `ZoneDatabaseConfig.java` | ⚠️ Caution | `@Value` annotations default to `10.173.48.130` (a private IP) |
| Secrets in version control | ❓ Unknown | `.gitignore` exists; whether the actual env file is committed is not verifiable from source alone |

**Evidence:** `application.properties:20-21`, `ZoneDatabaseConfig.java:20-29`

> **NOTE:** The `postgres` / `postgres` default credentials and hardcoded private IP `10.173.48.130` in `ZoneDatabaseConfig.java` are development-environment defaults. These must never be used in production.

---

## 9. Network and Dependency Security

| Item | Status | Evidence |
|---|---|---|
| HTTPS/TLS configuration | ❓ Unknown | No SSL config in `application.properties` or `SecurityConfig.java` |
| Spring Boot version | 4.1.1 (latest at audit time) | ✅ Current | `pom.xml:8` |
| PostgreSQL driver | Declared (runtime scope) | ✅ Implemented | `pom.xml:51-54` |
| Redis reactive dependency | Declared but unused | ⚠️ Unnecessary attack surface | `pom.xml:34-36` |
| CORS configuration | **Not found** | ⚠️ Missing | No `@CrossOrigin`, `WebMvcConfigurer.addCorsMappings`, or CORS bean found |

> **NOTE:** No CORS policy is defined. Spring Boot's default CORS behavior blocks cross-origin requests, but there is no explicit documented policy. If a frontend is expected to call this API, CORS must be explicitly configured.

---

## 10. Audit Logging

| Item | Status | Evidence |
|---|---|---|
| SLF4J logging in `ZoneDatabaseManager` | ✅ Implemented | `ZoneDatabaseManager.java:30` — logs zone routing decisions, warnings |
| SLF4J logging in `GlobalUpdateCheckHelper` | ✅ Implemented | `GlobalUpdateCheckHelper.java:35` — logs DB errors |
| Request/response access logging | ❓ Unknown | No access log or request interceptor found; `middleware` package is declared but missing |
| Authentication/authorization event logging | ⚠️ Missing | No Spring Security audit events configured |
| User identity in logs | ❓ Unknown | Log messages reference `family_id` and `districtCode` but no user/session identity |

---

## 11. Security Summary

| Category | Implemented | Missing | Notes |
|---|---|---|---|
| Authentication | ❌ None | All endpoints permitted | `SecurityConfig.java` allows all |
| Authorization (RBAC) | ❌ None | All endpoints permitted | No `.hasRole()` anywhere |
| CSRF | ✅ Disabled | — | Intentional for REST |
| SQL Injection prevention | ✅ Parameterized queries | — | Fully implemented |
| UUID input validation | ✅ Yes | — | Java + DB-level |
| Soft delete filtering | ✅ Yes | — | `is_deleted = false` on all queries |
| Exception message disclosure | ⚠️ Risk | Custom error handler | Raw messages in responses |
| Default credentials | ⚠️ Risk | — | `postgres`/`postgres` fallback |
| CORS policy | ❌ Not defined | Explicit config needed | No CORS rules found |
| HTTPS enforcement | ❓ Unknown | — | Not in source config |
| Rate limiting / throttling | ❓ Unknown | — | No config or filter found |
| Audit logging | ⚠️ Partial | Access/auth logs | Zone routing logged only |
