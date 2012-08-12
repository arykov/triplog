package com.ryaltech.ww.dao;

import java.util.Date;
import java.util.List;

public interface RiverDao {
	List<Outing> getUserOutings(long userId, Date startRange, Date endRange);
	Outing registerOuting(Outing outing);
	User registerUser(User user);
	River createRiver(River river);
	List<River> getRiversByName(String mask);
	
	

}
