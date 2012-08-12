package com.ryaltech.orm;

import javax.sql.DataSource;



import junit.framework.Assert;
import junit.framework.TestCase;

public class DefaultIdGeneratorCachingTest extends TestCase {
    public static class StubDBOutIdGenerator extends DefaultIdGenerator{
        int sequenceValue;
        @Override
        long getNextSequenceValue(DataSource dataSource){
            return sequenceValue++;
        }

    }
    public void testCaching(){
        StubDBOutIdGenerator idGenerator = new StubDBOutIdGenerator();
        for(int i=0;i<=DefaultIdGenerator.VALUES_PER_SEQUENCE_NUMBER*2;i++){
            Assert.assertEquals(i, idGenerator.generate(null, null));
        }
        Assert.assertEquals(3, idGenerator.sequenceValue);
    }

}
