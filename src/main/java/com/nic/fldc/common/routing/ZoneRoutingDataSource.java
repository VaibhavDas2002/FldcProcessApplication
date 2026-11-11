package com.nic.fldc.common.routing;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Multi-tenant dynamic DataSource router for Zone-partitioned databases.
 * Inspects ZoneContextHolder to select the appropriate read/write DataSource
 * for the caller's target LGD district.
 */
public class ZoneRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(ZoneRoutingDataSource.class);

    private final String defaultZone;

    public ZoneRoutingDataSource(String defaultZone, Map<Object, Object> targetDataSources, DataSource defaultTargetDataSource) {
        this.defaultZone = defaultZone;
        setTargetDataSources(targetDataSources);
        setDefaultTargetDataSource(defaultTargetDataSource);
        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String zoneKey = ZoneContextHolder.getZoneKey();
        if (zoneKey != null) {
            log.trace("Routing database connection to zone: {}", zoneKey);
            return zoneKey;
        }

        // Default fallback
        String mode = ZoneContextHolder.isReadOnly() ? "_read" : "_write";
        String fallbackKey = defaultZone + mode;
        log.trace("ZoneContextHolder is empty. Defaulting to: {}", fallbackKey);
        return fallbackKey;
    }
}

