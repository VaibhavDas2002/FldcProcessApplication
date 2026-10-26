package com.nic.FldcProcessApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import repository.familymemberRepository;
import service.StatusFLDC;
import service.ZoneDatabaseManager;
import util.ZoneDB;

@SpringBootTest
class FldcProcessApplicationTests {

	@Autowired(required = false)
	private StatusFLDC statusFLDC;

	@Autowired(required = false)
	private familymemberRepository memberRepository;

	@Autowired(required = false)
	private ZoneDatabaseManager zoneDatabaseManager;

	@Test
	void contextLoads() {
		assertNotNull(statusFLDC, "StatusFLDC bean should be loaded in Spring context");
		assertNotNull(memberRepository, "familymemberRepository bean should be loaded in Spring context");
		assertNotNull(zoneDatabaseManager, "ZoneDatabaseManager bean should be loaded in Spring context");
	}

	@Test
	void testZoneResolution() {
		assertNotNull(zoneDatabaseManager);

		// District 304 is configured in Zone 1
		assertEquals("zone1", zoneDatabaseManager.resolveZone(304));
		assertEquals("zone1_read", zoneDatabaseManager.getReadConnectionName(304));
		assertEquals("zone1_write", zoneDatabaseManager.getWriteConnectionName(304));

		// District 303 is configured in Zone 2
		assertEquals("zone2", zoneDatabaseManager.resolveZone(303));
		assertEquals("zone2_read", zoneDatabaseManager.getReadConnectionName(303));
		assertEquals("zone2_write", zoneDatabaseManager.getWriteConnectionName(303));

		// Unknown district falls back to default zone1
		assertEquals("zone1", zoneDatabaseManager.resolveZone(999999));

		// Active zones test
		List<String> activeZones = zoneDatabaseManager.activeZones();
		assertEquals(2, activeZones.size());
		assertTrue(activeZones.contains("zone1"));
		assertTrue(activeZones.contains("zone2"));
	}

	@Test
	void testDataSourceAndTemplateRetrieval() {
		assertNotNull(zoneDatabaseManager);

		assertNotNull(zoneDatabaseManager.getReadDataSource(304));
		assertNotNull(zoneDatabaseManager.getWriteDataSource(304));
		assertNotNull(zoneDatabaseManager.getReadDataSource(303));
		assertNotNull(zoneDatabaseManager.getWriteDataSource(303));

		assertNotNull(zoneDatabaseManager.getReadNamedParameterJdbcTemplate(304));
		assertNotNull(zoneDatabaseManager.getWriteNamedParameterJdbcTemplate(304));
		assertNotNull(zoneDatabaseManager.getReadNamedParameterJdbcTemplate(303));
		assertNotNull(zoneDatabaseManager.getWriteNamedParameterJdbcTemplate(303));

		assertNotNull(zoneDatabaseManager.getAuthReadDataSource());
		assertNotNull(zoneDatabaseManager.getAuthWriteDataSource());
		assertNotNull(zoneDatabaseManager.getAuthReadNamedParameterJdbcTemplate());
		assertNotNull(zoneDatabaseManager.getAuthWriteNamedParameterJdbcTemplate());
	}

	@Test
	void testStaticZoneDBFacade() {
		assertEquals("zone1", ZoneDB.resolveZone(304));
		assertEquals("zone2", ZoneDB.resolveZone(303));
		assertEquals("zone1_read", ZoneDB.getReadConnectionName(304));
		assertEquals("zone2_write", ZoneDB.getWriteConnectionName(303));
		assertNotNull(ZoneDB.getReadNamedParameterJdbcTemplate(304));
		assertNotNull(ZoneDB.getWriteNamedParameterJdbcTemplate(303));
	}

	@Autowired(required = false)
	private processWorkflow.ProcessApplication processApplication;

	@Test
	void testGetTestEndpoint() {
		assertNotNull(processApplication, "ProcessApplication controller should be loaded");
		org.springframework.http.ResponseEntity<?> response = processApplication.getTest();
		assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		@SuppressWarnings("unchecked")
		java.util.Map<String, Object> body = (java.util.Map<String, Object>) response.getBody();
		assertEquals("Family members retrieved successfully", body.get("message"));
		assertEquals(true, body.get("status"));
		assertEquals(200, body.get("statusCode"));
	}

}
