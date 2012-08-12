package com.ryaltech.experiments;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

public class ExperimentTest {

	@Test
	public void testUuidLenght() {
		assertEquals(36, UUID.randomUUID().toString().length());
	}

}
