package com.ryaltech.log;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * This class exists to produce LogEvents that can be formatted to proper
 * class/method/line number Is tested on log4j 1.2.7
 * 
 * @author Rykov
 */

public class Log4jLoggerFactory implements LoggerFactory {

    @Override
    public Logger makeNewLoggerInstance(String name) {
        return new Log4jLogger(name);
    }

}
