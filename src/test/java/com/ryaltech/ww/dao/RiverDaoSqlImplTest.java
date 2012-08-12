package com.ryaltech.ww.dao;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hamcrest.beans.SamePropertyValuesAs;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.ryaltech.ww.dao.User.AuthProvider;

public class RiverDaoSqlImplTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");

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
	public void testOuting() throws Exception{
		try{
			dao.registerOuting(new Outing().setUserId(1).setRiverId(2).setStartTimeStamp(new Date()));
			fail("should've failed");
		}catch(DataIntegrityViolationException ex){			
		}
		
		River gull = dao.createRiver(new River().setRiverName("Gull"));
		River ottawa = dao.createRiver(new River().setRiverName("Ottawa"));
		User john = dao.registerUser(new User().setAuthProvider(AuthProvider.GOOGLE).setAuthProviderId("x@gmail.com").setUserName("john"));
		User mike = dao.registerUser(new User().setAuthProvider(AuthProvider.GOOGLE).setAuthProviderId("x@gmail.com").setUserName("mike"));
		Outing outing1 = dao.registerOuting(new Outing().setUserId(mike.getUserId()).setRiverId(ottawa.getId()).setStartTimeStamp(sdf.parse("2010-JUL-01")).setNotes("123"));
		Outing outing2 = dao.registerOuting(new Outing().setUserId(mike.getUserId()).setRiverId(gull.getId()).setStartTimeStamp(sdf.parse("2010-JUL-03")).setNotes("321"));
		
		
		List<Outing> johnOutings = dao.getUserOutings(john.getUserId(), sdf.parse("1999-APR-01"), sdf.parse("2999-APR-01"));
		assertNotNull(johnOutings);
		assertEquals(0, johnOutings.size());
		
		List<Outing> mikeOutings = dao.getUserOutings(mike.getUserId(), sdf.parse("2010-JUN-30"),sdf.parse("2010-JUL-04"));
		assertNotNull(mikeOutings);
		assertEquals(2, mikeOutings.size());
		
		assertThat(mikeOutings.get(0), new SamePropertyValuesAs<Outing>(outing1));
		assertThat(mikeOutings.get(1), new SamePropertyValuesAs<Outing>(outing2));
		
		
		mikeOutings = dao.getUserOutings(mike.getUserId(), sdf.parse("2010-JUN-30"), sdf.parse("2010-JUL-02"));
		assertNotNull(mikeOutings);
		assertEquals(1, mikeOutings.size());
		assertThat(mikeOutings.get(0), new SamePropertyValuesAs<Outing>(outing1));
		
		mikeOutings = dao.getUserOutings(mike.getUserId(), sdf.parse("2010-JUL-02"), sdf.parse("2010-JUL-04"));
		assertNotNull(mikeOutings);
		assertEquals(1, mikeOutings.size());
		assertThat(mikeOutings.get(0), new SamePropertyValuesAs<Outing>(outing2));
		
		
	}

}
