package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import service.ZoneDatabaseManager;

/**
 * Static Facade mirroring Laravel's ZoneDB Facade:
 * {@code ZoneDB::getReadConnection($districtId)}
 */
@Component
public class ZoneDB {

    private static ZoneDatabaseManager manager;

    public ZoneDB(ZoneDatabaseManager zoneDatabaseManager) {
        ZoneDB.manager = zoneDatabaseManager;
    }

    public static ZoneDatabaseManager getManager() {
        if (manager == null) {
            throw new IllegalStateException("ZoneDatabaseManager has not been initialized yet.");
        }
        return manager;
    }

    public static String resolveZone() {
        return getManager().resolveZone();
    }

    public static String resolveZone(Integer districtId) {
        return getManager().resolveZone(districtId);
    }

    public static String currentZone() {
        return getManager().currentZone();
    }

    public static List<String> activeZones() {
        return getManager().activeZones();
    }

    public static String getReadConnectionName() {
        return getManager().getReadConnectionName();
    }

    public static String getWriteConnectionName() {
        return getManager().getWriteConnectionName();
    }

    public static String getConnectionName(String mode) {
        return getManager().getConnectionName(mode);
    }

    public static String getReadConnectionName(Integer districtId) {
        return getManager().getReadConnectionName(districtId);
    }

    public static String getWriteConnectionName(Integer districtId) {
        return getManager().getWriteConnectionName(districtId);
    }

    public static String getConnectionName(String mode, Integer districtId) {
        return getManager().getConnectionName(mode, districtId);
    }

    public static Connection getReadConnection() throws SQLException {
        return getManager().getReadConnection();
    }

    public static Connection getWriteConnection() throws SQLException {
        return getManager().getWriteConnection();
    }

    public static Connection getConnection(String mode) throws SQLException {
        return getManager().getConnection(mode);
    }

    public static DataSource getReadDataSource() {
        return getManager().getReadDataSource();
    }

    public static DataSource getWriteDataSource() {
        return getManager().getWriteDataSource();
    }

    public static DataSource getDataSource(String mode) {
        return getManager().getDataSource(mode);
    }

    public static NamedParameterJdbcTemplate getReadNamedParameterJdbcTemplate() {
        return getManager().getReadNamedParameterJdbcTemplate();
    }

    public static NamedParameterJdbcTemplate getWriteNamedParameterJdbcTemplate() {
        return getManager().getWriteNamedParameterJdbcTemplate();
    }

    public static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String mode) {
        return getManager().getNamedParameterJdbcTemplate(mode);
    }

    public static JdbcTemplate getReadJdbcTemplate() {
        return getManager().getReadJdbcTemplate();
    }

    public static JdbcTemplate getWriteJdbcTemplate() {
        return getManager().getWriteJdbcTemplate();
    }

    public static JdbcTemplate getJdbcTemplate(String mode) {
        return getManager().getJdbcTemplate(mode);
    }

    public static Connection getReadConnection(Integer districtId) throws SQLException {
        return getManager().getReadConnection(districtId);
    }

    public static Connection getWriteConnection(Integer districtId) throws SQLException {
        return getManager().getWriteConnection(districtId);
    }

    public static Connection getConnection(String mode, Integer districtId) throws SQLException {
        return getManager().getConnection(mode, districtId);
    }

    public static DataSource getReadDataSource(Integer districtId) {
        return getManager().getReadDataSource(districtId);
    }

    public static DataSource getWriteDataSource(Integer districtId) {
        return getManager().getWriteDataSource(districtId);
    }

    public static DataSource getDataSource(String mode, Integer districtId) {
        return getManager().getDataSource(mode, districtId);
    }

    public static NamedParameterJdbcTemplate getReadNamedParameterJdbcTemplate(Integer districtId) {
        return getManager().getReadNamedParameterJdbcTemplate(districtId);
    }

    public static NamedParameterJdbcTemplate getWriteNamedParameterJdbcTemplate(Integer districtId) {
        return getManager().getWriteNamedParameterJdbcTemplate(districtId);
    }

    public static NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String mode, Integer districtId) {
        return getManager().getNamedParameterJdbcTemplate(mode, districtId);
    }

    public static JdbcTemplate getReadJdbcTemplate(Integer districtId) {
        return getManager().getReadJdbcTemplate(districtId);
    }

    public static JdbcTemplate getWriteJdbcTemplate(Integer districtId) {
        return getManager().getWriteJdbcTemplate(districtId);
    }

    public static JdbcTemplate getJdbcTemplate(String mode, Integer districtId) {
        return getManager().getJdbcTemplate(mode, districtId);
    }
}
