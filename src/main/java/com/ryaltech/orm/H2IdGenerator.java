package com.ryaltech.orm;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * H2/HSQLDB sequence based id generator. 
 *
 * @author Alex Rykov
 *
 */
public class H2IdGenerator implements IdGenerator {

    @Override
    public long generate(DataSource dataSource, String table) {
        return new JdbcTemplate(dataSource).queryForLong("call next value for wfl_orm_seq");
    }

}
