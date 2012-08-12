package com.ryaltech.log;

import org.apache.log4j.Priority;

/**
 * This class exists to produce LogEvents that can be formatted to proper
 * class/method/line number Is tested on log4j 1.2.7
 *
 * @author Rykov
 */
public class Log4jLogger extends org.apache.log4j.Logger {
    private static final String FQCN = Logger.class.getName();

    protected Log4jLogger(String name) {
        super(name);
    }

    @Override
    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        super.forcedLog(FQCN, level, message, t);
    }

}
