package com.ryaltech.ww.dao;
import static junit.framework.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class RiverDaoSqlImplTest {

	RiverDao dao;
	@Before
	public void setUp(){
		dao = new TestRiverDaoSqlImpl();
	}
	@Test
	public void testRiverCreateRetrieveByName() {
		dao.createRiver(new River().setRiverName("Volga"));
		dao.createRiver(new River().setRiverName("Oka"));
		List<River> riverList = dao.getRiversByName("olg");
		assertNotNull(riverList);
		assertEquals(1, riverList.size());
		assertEquals("Volga", riverList.get(0).getRiverName());
		
		riverList = dao.getRiversByName("nothingfound");
		assertNotNull(riverList);
		assertEquals(0, riverList.size());
		
	}
	
	@Test
	public void testOuting() {
		try{
			dao.registerOuting(new Outing().setUserId(1).setRiverId(2).setStartTimeStamp(new Date()));
			fail("should've failed");
		}catch(DataIntegrityViolationException ex){
			
		}
		
	}

}
