package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import jakarta.annotation.PostConstruct;

@Service
public class ZoneDatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(ZoneDatabaseManager.class);

    @Value("${zone.database.active-zones:2}")
    private int activeZones;

    @Value("${zone.database.default-zone:zone1}")
    private String defaultZone;

    @Value("${zone.database.zone1.district-codes:306,304,664,305,307,308,309,310,311,312,313,314}")
    private String zone1DistrictsRaw;

    @Value("${zone.database.zone2.district-codes:303,703,702,315,316,317,318,319,320,704,306,321}")
    private String zone2DistrictsRaw;

    private final DataSource zone1ReadDataSource;
    private final DataSource zone1WriteDataSource;
    private final DataSource zone2ReadDataSource;
    private final DataSource zone2WriteDataSource;
    private final DataSource authReadDataSource;
    private final DataSource authWriteDataSource;

    private final NamedParameterJdbcTemplate zone1ReadNamedJdbcTemplate;
    private final NamedParameterJdbcTemplate zone1WriteNamedJdbcTemplate;
    private final NamedParameterJdbcTemplate zone2ReadNamedJdbcTemplate;
    private final NamedParameterJdbcTemplate zone2WriteNamedJdbcTemplate;
    private final NamedParameterJdbcTemplate authReadNamedJdbcTemplate;
    private final NamedParameterJdbcTemplate authWriteNamedJdbcTemplate;

    private final JdbcTemplate zone1ReadJdbcTemplate;
    private final JdbcTemplate zone1WriteJdbcTemplate;
    private final JdbcTemplate zone2ReadJdbcTemplate;
    private final JdbcTemplate zone2WriteJdbcTemplate;
    private final JdbcTemplate authReadJdbcTemplate;
    private final JdbcTemplate authWriteJdbcTemplate;

    /**
     * Cached zone lookup map: [district_id => zone_name]
     */
    private final Map<Integer, String> districtZoneMap = new ConcurrentHashMap<>();

    public ZoneDatabaseManager(
            @Qualifier("zone1ReadDataSource") DataSource zone1ReadDataSource,
            @Qualifier("zone1WriteDataSource") DataSource zone1WriteDataSource,
            @Qualifier("zone2ReadDataSource") DataSource zone2ReadDataSource,
            @Qualifier("zone2WriteDataSource") DataSource zone2WriteDataSource,
            @Qualifier("authReadDataSource") DataSource authReadDataSource,
            @Qualifier("authWriteDataSource") DataSource authWriteDataSource,
            @Qualifier("zone1ReadNamedJdbcTemplate") NamedParameterJdbcTemplate zone1ReadNamedJdbcTemplate,
            @Qualifier("zone1WriteNamedJdbcTemplate") NamedParameterJdbcTemplate zone1WriteNamedJdbcTemplate,
            @Qualifier("zone2ReadNamedJdbcTemplate") NamedParameterJdbcTemplate zone2ReadNamedJdbcTemplate,
            @Qualifier("zone2WriteNamedJdbcTemplate") NamedParameterJdbcTemplate zone2WriteNamedJdbcTemplate,
            @Qualifier("authReadNamedJdbcTemplate") NamedParameterJdbcTemplate authReadNamedJdbcTemplate,
            @Qualifier("authWriteNamedJdbcTemplate") NamedParameterJdbcTemplate authWriteNamedJdbcTemplate,
            @Qualifier("zone1ReadJdbcTemplate") JdbcTemplate zone1ReadJdbcTemplate,
            @Qualifier("zone1WriteJdbcTemplate") JdbcTemplate zone1WriteJdbcTemplate,
            @Qualifier("zone2ReadJdbcTemplate") JdbcTemplate zone2ReadJdbcTemplate,
            @Qualifier("zone2WriteJdbcTemplate") JdbcTemplate zone2WriteJdbcTemplate,
            @Qualifier("authReadJdbcTemplate") JdbcTemplate authReadJdbcTemplate,
            @Qualifier("authWriteJdbcTemplate") JdbcTemplate authWriteJdbcTemplate) {

        this.zone1ReadDataSource = zone1ReadDataSource;
        this.zone1WriteDataSource = zone1WriteDataSource;
        this.zone2ReadDataSource = zone2ReadDataSource;
        this.zone2WriteDataSource = zone2WriteDataSource;
        this.authReadDataSource = authReadDataSource;
        this.authWriteDataSource = authWriteDataSource;

        this.zone1ReadNamedJdbcTemplate = zone1ReadNamedJdbcTemplate;
        this.zone1WriteNamedJdbcTemplate = zone1WriteNamedJdbcTemplate;
        this.zone2ReadNamedJdbcTemplate = zone2ReadNamedJdbcTemplate;
        this.zone2WriteNamedJdbcTemplate = zone2WriteNamedJdbcTemplate;
        this.authReadNamedJdbcTemplate = authReadNamedJdbcTemplate;
        this.authWriteNamedJdbcTemplate = authWriteNamedJdbcTemplate;

        this.zone1ReadJdbcTemplate = zone1ReadJdbcTemplate;
        this.zone1WriteJdbcTemplate = zone1WriteJdbcTemplate;
        this.zone2ReadJdbcTemplate = zone2ReadJdbcTemplate;
        this.zone2WriteJdbcTemplate = zone2WriteJdbcTemplate;
        this.authReadJdbcTemplate = authReadJdbcTemplate;
        this.authWriteJdbcTemplate = authWriteJdbcTemplate;
    }

    @PostConstruct
    public void init() {
        populateDistrictMap("zone1", zone1DistrictsRaw);
        populateDistrictMap("zone2", zone2DistrictsRaw);
        log.info("ZoneDatabaseManager initialized with {} mapped districts across active zones ({})",
                districtZoneMap.size(), activeZones);
    }

    private void populateDistrictMap(String zoneName, String districtCodesRaw) {
        if (districtCodesRaw == null || districtCodesRaw.isBlank()) {
            return;
        }
        String clean = districtCodesRaw.replace("\"", "").replace("'", "").trim();
        Arrays.stream(clean.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(code -> {
                    try {
                        districtZoneMap.put(Integer.parseInt(code), zoneName);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid district code '{}' in {} config", code, zoneName);
                    }
                });
    }

    /**
     * Get the district ID from the current session if available.
     */
    public Integer getDistrictId() {
        try {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                Object districtObj = attrs.getAttribute("lgd_session.district_id", RequestAttributes.SCOPE_SESSION);
                if (districtObj == null) {
                    districtObj = attrs.getAttribute("district_id", RequestAttributes.SCOPE_SESSION);
                }
                if (districtObj != null) {
                    return Integer.valueOf(districtObj.toString());
                }
            }
        } catch (Exception e) {
            log.debug("Could not resolve districtId from session: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Resolve the zone name for a given district ID.
     *
     * @param districtId Explicit district ID; if null, read from session or fallback to default.
     * @return Zone name (e.g. 'zone1', 'zone2')
     */
    public String resolveZone(Integer districtId) {
        if (districtId == null) {
            districtId = getDistrictId();
        }

        if (districtId == null) {
            return defaultZone;
        }

        String zone = districtZoneMap.get(districtId);
        if (zone != null) {
            try {
                int zoneNumber = Integer.parseInt(zone.replaceAll("[^0-9]", ""));
                if (zoneNumber > activeZones) {
                    log.warn("ZoneDatabaseManager: Zone {} is inactive (active_zones={}). Falling back to default.",
                            zone, activeZones);
                    return defaultZone;
                }
            } catch (Exception ignored) {
            }
            return zone;
        }

        log.warn("ZoneDatabaseManager: District ID {} not found in any zone config. Using default zone.", districtId);
        return defaultZone;
    }

    /**
     * Get the connection name for a given mode and district (e.g. 'zone1_read', 'zone2_write').
     */
    public String getConnectionName(String mode, Integer districtId) {
        String zone = resolveZone(districtId);
        String suffix = "write".equalsIgnoreCase(mode) ? "_write" : "_read";
        return zone + suffix;
    }

    public String resolveZone() {
        return resolveZone(null);
    }

    public String getConnectionName(String mode) {
        return getConnectionName(mode, null);
    }

    public String getReadConnectionName() {
        return getReadConnectionName(null);
    }

    public String getWriteConnectionName() {
        return getWriteConnectionName(null);
    }

    public String getReadConnectionName(Integer districtId) {
        return getConnectionName("read", districtId);
    }

    public String getWriteConnectionName(Integer districtId) {
        return getConnectionName("write", districtId);
    }

    // ── DataSource Accessors ─────────────────────────────────────────────
    public DataSource getReadDataSource() {
        return getReadDataSource(null);
    }

    public DataSource getWriteDataSource() {
        return getWriteDataSource(null);
    }

    public DataSource getDataSource(String mode) {
        return getDataSource(mode, null);
    }

    public Connection getReadConnection() throws SQLException {
        return getReadConnection(null);
    }

    public Connection getWriteConnection() throws SQLException {
        return getWriteConnection(null);
    }

    public Connection getConnection(String mode) throws SQLException {
        return getConnection(mode, null);
    }

    public NamedParameterJdbcTemplate getReadNamedParameterJdbcTemplate() {
        return getReadNamedParameterJdbcTemplate(null);
    }

    public NamedParameterJdbcTemplate getWriteNamedParameterJdbcTemplate() {
        return getWriteNamedParameterJdbcTemplate(null);
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String mode) {
        return getNamedParameterJdbcTemplate(mode, null);
    }

    public JdbcTemplate getReadJdbcTemplate() {
        return getReadJdbcTemplate(null);
    }

    public JdbcTemplate getWriteJdbcTemplate() {
        return getWriteJdbcTemplate(null);
    }

    public JdbcTemplate getJdbcTemplate(String mode) {
        return getJdbcTemplate(mode, null);
    }
    public DataSource getReadDataSource(Integer districtId) {
        String zone = resolveZone(districtId);
        return "zone2".equalsIgnoreCase(zone) ? zone2ReadDataSource : zone1ReadDataSource;
    }

    public DataSource getWriteDataSource(Integer districtId) {
        String zone = resolveZone(districtId);
        return "zone2".equalsIgnoreCase(zone) ? zone2WriteDataSource : zone1WriteDataSource;
    }

    public DataSource getDataSource(String mode, Integer districtId) {
        return "write".equalsIgnoreCase(mode) ? getWriteDataSource(districtId) : getReadDataSource(districtId);
    }

    // ── java.sql.Connection Accessors ────────────────────────────────────
    public Connection getReadConnection(Integer districtId) throws SQLException {
        return getReadDataSource(districtId).getConnection();
    }

    public Connection getWriteConnection(Integer districtId) throws SQLException {
        return getWriteDataSource(districtId).getConnection();
    }

    public Connection getConnection(String mode, Integer districtId) throws SQLException {
        return getDataSource(mode, districtId).getConnection();
    }

    // ── NamedParameterJdbcTemplate Accessors ──────────────────────────────
    public NamedParameterJdbcTemplate getReadNamedParameterJdbcTemplate(Integer districtId) {
        String zone = resolveZone(districtId);
        return "zone2".equalsIgnoreCase(zone) ? zone2ReadNamedJdbcTemplate : zone1ReadNamedJdbcTemplate;
    }

    public NamedParameterJdbcTemplate getWriteNamedParameterJdbcTemplate(Integer districtId) {
        String zone = resolveZone(districtId);
        return "zone2".equalsIgnoreCase(zone) ? zone2WriteNamedJdbcTemplate : zone1WriteNamedJdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String mode, Integer districtId) {
        return "write".equalsIgnoreCase(mode)
                ? getWriteNamedParameterJdbcTemplate(districtId)
                : getReadNamedParameterJdbcTemplate(districtId);
    }

    // ── JdbcTemplate Accessors ───────────────────────────────────────────
    public JdbcTemplate getReadJdbcTemplate(Integer districtId) {
        String zone = resolveZone(districtId);
        return "zone2".equalsIgnoreCase(zone) ? zone2ReadJdbcTemplate : zone1ReadJdbcTemplate;
    }

    public JdbcTemplate getWriteJdbcTemplate(Integer districtId) {
        String zone = resolveZone(districtId);
        return "zone2".equalsIgnoreCase(zone) ? zone2WriteJdbcTemplate : zone1WriteJdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate(String mode, Integer districtId) {
        return "write".equalsIgnoreCase(mode)
                ? getWriteJdbcTemplate(districtId)
                : getReadJdbcTemplate(districtId);
    }

    // ── Consolidate / Auth Accessors ─────────────────────────────────────
    public DataSource getAuthReadDataSource() {
        return authReadDataSource;
    }

    public DataSource getAuthWriteDataSource() {
        return authWriteDataSource;
    }

    public Connection getAuthReadConnection() throws SQLException {
        return authReadDataSource.getConnection();
    }

    public Connection getAuthWriteConnection() throws SQLException {
        return authWriteDataSource.getConnection();
    }

    public NamedParameterJdbcTemplate getAuthReadNamedParameterJdbcTemplate() {
        return authReadNamedJdbcTemplate;
    }

    public NamedParameterJdbcTemplate getAuthWriteNamedParameterJdbcTemplate() {
        return authWriteNamedJdbcTemplate;
    }

    public JdbcTemplate getAuthReadJdbcTemplate() {
        return authReadJdbcTemplate;
    }

    public JdbcTemplate getAuthWriteJdbcTemplate() {
        return authWriteJdbcTemplate;
    }

    // ── Zone Utility Methods ─────────────────────────────────────────────
    public String currentZone() {
        return resolveZone(null);
    }

    public List<String> activeZones() {
        List<String> active = new ArrayList<>();
        for (int i = 1; i <= activeZones; i++) {
            active.add("zone" + i);
        }
        return Collections.unmodifiableList(active);
    }

    public Map<Integer, String> getDistrictZoneMap() {
        return Collections.unmodifiableMap(new HashMap<>(districtZoneMap));
    }
}
