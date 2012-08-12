package com.ryaltech.ww.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import sun.font.LayoutPathImpl.EndType;

import com.ryaltech.orm.EnumStringConverter;
import com.ryaltech.orm.Mapping;
import com.ryaltech.orm.StringConverter;
import com.ryaltech.orm.TimestampConverter;
import com.ryaltech.sql.SelectBuilder;
import com.ryaltech.ww.dao.User.AuthProvider;





public class RiverDaoSqlImpl implements RiverDao {
	
	private DataSource dataSource;
	public static final String USERS_TABLE="users"; 
	public static final String RIVERS_TABLE="rivers";
	public static final String OUTINGS_TABLE="outings";
	
    private Mapping<River> riverMapping = new Mapping<River>(River.class, RIVERS_TABLE)
    		.setIdColumn("river_id")
    		.add("riverName", "river_name");
    
    private Mapping<User> userMapping = new Mapping<User>(User.class, USERS_TABLE)    
    		.setIdColumn("user_id")    
    		.add("userName", "user_name")
    		.add("authProviderId", "auth_provider_id")
    		.add("authProvider", "auth_provider", EnumStringConverter.create(User.AuthProvider.class));
    
    private Mapping<Outing> outingMapping = new Mapping<Outing>(Outing.class, OUTINGS_TABLE)
    	    .setIdColumn("outing_id")
    	    .add("userId", "user_id")
    	    .add("riverId", "river_id")
    	    .add("startTimeStamp", "start_timestamp", new TimestampConverter())
    	    .add("endTimeStamp", "end_timestamp", new TimestampConverter())
    	    .add("notes", "notes");
    
    
    

    DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Outing> getUserOutings(long userId, Date startRange, Date endRange) {
		return
		outingMapping.createQuery(dataSource)
		.where("user_id=", userId)
		.where("start_timestamp>", new Timestamp(startRange.getTime()))
		.where("start_timestamp<", new Timestamp(endRange.getTime()))
		.getResultList();
	}

	@Override
	public Outing registerOuting(Outing outing) {
		return outingMapping.insert(dataSource, outing);

		
	}

	@Override
	public User registerUser(User user) {
		return userMapping.insert(dataSource,user);
		
	}

	@Override
	public River createRiver(River river) {
		return riverMapping.insert(dataSource, river);
	}

	@Override
	public List<River> getRiversByName(String mask) {
		return riverMapping.createQuery(dataSource).where(String.format("river_name like '%%%s%%'", mask)).getResultList();
	}

}
