package com.ryaltech.orm;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Generates unique ids based on DB Oracle sequence. This implementation treats sequence number
 * as a number of a block of unique ids, that has "VALUES_PER_SEQUENCE_NUMBER" unique values in it.
 * For example, block 3 with VALUES_PER_SEQUENCE_NUMBER = 1000 will include  [3000...3999]
 *
 * @author Alex Rykov
 *
 */
public class DefaultIdGenerator implements IdGenerator{

    public static final long VALUES_PER_SEQUENCE_NUMBER = 1000;
    private long currentSequenceIntervalEnd = -1;
    private long currentUniqueId = 0;

    @Override
    public synchronized long generate(DataSource dataSource, String table) {
        if (currentUniqueId >= currentSequenceIntervalEnd) {
            long sequenceNumber = getNextSequenceValue(dataSource);
            currentUniqueId = sequenceNumber * VALUES_PER_SEQUENCE_NUMBER;
            currentSequenceIntervalEnd = (sequenceNumber + 1) * VALUES_PER_SEQUENCE_NUMBER;
        }

        return currentUniqueId++;
    }


    long getNextSequenceValue(DataSource dataSource) {
        return new JdbcTemplate(dataSource).queryForLong("select orm_seq.nextval from dual");
    }

}
