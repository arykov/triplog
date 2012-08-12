package com.ryaltech.orm;

import javax.sql.DataSource;

import com.ryaltech.orm.IdGenerator;

public class DummyIdGenerator implements IdGenerator {

    private long nextId = 1;

    @Override
    public synchronized long generate(DataSource dataSource, String table) {
        return nextId++;
    }

}
