package com.ryaltech.orm;

import com.ryaltech.orm.Entity;

/**
 * Example non-versioned entity, for testing.
 */
public class LogEntry implements Entity {

    private long id;

    private String message;

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
