package com.ryaltech.orm;

import javax.sql.DataSource;

/**
 * Interface to generate unique DB based ids
 *
 * @author Alex Rykov
 *
 */
public interface IdGenerator {

    /**
     * Generates unique ids.
     * @param dataSource that points to schema with tables and sequnces
     * @param table table for which ID is being generated
     * @return
     */
    public long generate(DataSource dataSource, String table);

}
