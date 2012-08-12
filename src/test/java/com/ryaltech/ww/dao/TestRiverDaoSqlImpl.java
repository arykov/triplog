package com.ryaltech.ww.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.ryaltech.util.IOUtils;

public class TestRiverDaoSqlImpl extends RiverDaoSqlImpl {
	public final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	public final String JDBC_URL_TRACING = JDBC_URL
			+ ";TRACE_LEVEL_FILE=3;TRACE_LEVEL_SYSTEM_OUT=3";

	public TestRiverDaoSqlImpl() {
		this(false);
	}

	public TestRiverDaoSqlImpl(boolean tracing) {

		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (tracing)
			setDataSource(new DriverManagerDataSource(JDBC_URL_TRACING));
		else
			setDataSource(new DriverManagerDataSource(JDBC_URL));

		JdbcTemplate t = new JdbcTemplate(getDataSource());

		String drop_ddl = IOUtils.toStringUtf8(getClass().getSuperclass()
				.getResourceAsStream("drop_ddl.sql"));
		String create_ddl = IOUtils.toStringUtf8(getClass().getSuperclass()
				.getResourceAsStream("create_ddl.sql"));

		executeDDL(t, drop_ddl, false);
		executeDDL(t, create_ddl, true);

	}

	private void executeDDL(JdbcTemplate t, String create_ddl,
			boolean throwExceptions) {
		for (String s : create_ddl.split(";")) {
			try {
				if (s != null && s.trim().length() > 0) {
					t.update(s);
				}
			} catch (RuntimeException ex) {
				if (throwExceptions) {
					throw ex;
				} else {
					ex.printStackTrace();
				}
			}

		}
	}
}
