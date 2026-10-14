package config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class ZoneDatabaseConfig {

    // ── Zone 1 ──────────────────────────────────────────────────────────
    @Value("${db.zone1.read.host:10.173.48.130}")
    private String zone1ReadHost;
    @Value("${db.zone1.read.port:5432}")
    private int zone1ReadPort;
    @Value("${db.zone1.read.database:annapurna_z1}")
    private String zone1ReadDatabase;
    @Value("${db.zone1.read.username:postgres}")
    private String zone1ReadUsername;
    @Value("${db.zone1.read.password:postgres}")
    private String zone1ReadPassword;

    @Value("${db.zone1.write.host:10.173.48.130}")
    private String zone1WriteHost;
    @Value("${db.zone1.write.port:5432}")
    private int zone1WritePort;
    @Value("${db.zone1.write.database:annapurna_z1}")
    private String zone1WriteDatabase;
    @Value("${db.zone1.write.username:postgres}")
    private String zone1WriteUsername;
    @Value("${db.zone1.write.password:postgres}")
    private String zone1WritePassword;

    // ── Zone 2 ──────────────────────────────────────────────────────────
    @Value("${db.zone2.read.host:10.173.48.130}")
    private String zone2ReadHost;
    @Value("${db.zone2.read.port:5432}")
    private int zone2ReadPort;
    @Value("${db.zone2.read.database:annapurna_z2}")
    private String zone2ReadDatabase;
    @Value("${db.zone2.read.username:postgres}")
    private String zone2ReadUsername;
    @Value("${db.zone2.read.password:postgres}")
    private String zone2ReadPassword;

    @Value("${db.zone2.write.host:10.173.48.130}")
    private String zone2WriteHost;
    @Value("${db.zone2.write.port:5432}")
    private int zone2WritePort;
    @Value("${db.zone2.write.database:annapurna_z2}")
    private String zone2WriteDatabase;
    @Value("${db.zone2.write.username:postgres}")
    private String zone2WriteUsername;
    @Value("${db.zone2.write.password:postgres}")
    private String zone2WritePassword;

    // ── Auth / Consolidate ───────────────────────────────────────────────
    @Value("${db.auth.read.host:10.173.48.130}")
    private String authReadHost;
    @Value("${db.auth.read.port:5432}")
    private int authReadPort;
    @Value("${db.auth.read.database:annapurna_consolidate}")
    private String authReadDatabase;
    @Value("${db.auth.read.username:postgres}")
    private String authReadUsername;
    @Value("${db.auth.read.password:postgres}")
    private String authReadPassword;

    @Value("${db.auth.write.host:10.173.48.130}")
    private String authWriteHost;
    @Value("${db.auth.write.port:5432}")
    private int authWritePort;
    @Value("${db.auth.write.database:annapurna_consolidate}")
    private String authWriteDatabase;
    @Value("${db.auth.write.username:postgres}")
    private String authWriteUsername;
    @Value("${db.auth.write.password:postgres}")
    private String authWritePassword;

    private HikariDataSource buildDataSource(String host, int port, String database, String username, String password, String poolName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setPoolName(poolName);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }

    // ── DataSources ──────────────────────────────────────────────────────
    @Primary
    @Bean(name = {"zone1ReadDataSource", "dataSource"})
    public DataSource zone1ReadDataSource() {
        return buildDataSource(zone1ReadHost, zone1ReadPort, zone1ReadDatabase, zone1ReadUsername, zone1ReadPassword, "HikariPool-Zone1Read");
    }

    @Bean(name = "zone1WriteDataSource")
    public DataSource zone1WriteDataSource() {
        return buildDataSource(zone1WriteHost, zone1WritePort, zone1WriteDatabase, zone1WriteUsername, zone1WritePassword, "HikariPool-Zone1Write");
    }

    @Bean(name = "zone2ReadDataSource")
    public DataSource zone2ReadDataSource() {
        return buildDataSource(zone2ReadHost, zone2ReadPort, zone2ReadDatabase, zone2ReadUsername, zone2ReadPassword, "HikariPool-Zone2Read");
    }

    @Bean(name = "zone2WriteDataSource")
    public DataSource zone2WriteDataSource() {
        return buildDataSource(zone2WriteHost, zone2WritePort, zone2WriteDatabase, zone2WriteUsername, zone2WritePassword, "HikariPool-Zone2Write");
    }

    @Bean(name = {"authReadDataSource", "psql_conReadDataSource"})
    public DataSource authReadDataSource() {
        return buildDataSource(authReadHost, authReadPort, authReadDatabase, authReadUsername, authReadPassword, "HikariPool-AuthRead");
    }

    @Bean(name = {"authWriteDataSource", "psql_conWriteDataSource"})
    public DataSource authWriteDataSource() {
        return buildDataSource(authWriteHost, authWritePort, authWriteDatabase, authWriteUsername, authWritePassword, "HikariPool-AuthWrite");
    }

    // ── NamedParameterJdbcTemplates ──────────────────────────────────────
    @Primary
    @Bean(name = "primaryNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate primaryNamedParameterJdbcTemplate(@Qualifier("zone1ReadDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "zone1ReadNamedJdbcTemplate")
    public NamedParameterJdbcTemplate zone1ReadNamedJdbcTemplate(@Qualifier("zone1ReadDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "zone1WriteNamedJdbcTemplate")
    public NamedParameterJdbcTemplate zone1WriteNamedJdbcTemplate(@Qualifier("zone1WriteDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "zone2ReadNamedJdbcTemplate")
    public NamedParameterJdbcTemplate zone2ReadNamedJdbcTemplate(@Qualifier("zone2ReadDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "zone2WriteNamedJdbcTemplate")
    public NamedParameterJdbcTemplate zone2WriteNamedJdbcTemplate(@Qualifier("zone2WriteDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "authReadNamedJdbcTemplate")
    public NamedParameterJdbcTemplate authReadNamedJdbcTemplate(@Qualifier("authReadDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "authWriteNamedJdbcTemplate")
    public NamedParameterJdbcTemplate authWriteNamedJdbcTemplate(@Qualifier("authWriteDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    // ── JdbcTemplates ────────────────────────────────────────────────────
    @Primary
    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("zone1ReadDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "zone1ReadJdbcTemplate")
    public JdbcTemplate zone1ReadJdbcTemplate(@Qualifier("zone1ReadDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "zone1WriteJdbcTemplate")
    public JdbcTemplate zone1WriteJdbcTemplate(@Qualifier("zone1WriteDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "zone2ReadJdbcTemplate")
    public JdbcTemplate zone2ReadJdbcTemplate(@Qualifier("zone2ReadDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "zone2WriteJdbcTemplate")
    public JdbcTemplate zone2WriteJdbcTemplate(@Qualifier("zone2WriteDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "authReadJdbcTemplate")
    public JdbcTemplate authReadJdbcTemplate(@Qualifier("authReadDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "authWriteJdbcTemplate")
    public JdbcTemplate authWriteJdbcTemplate(@Qualifier("authWriteDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
