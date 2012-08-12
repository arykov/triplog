package com.ryaltech.orm;

import com.ryaltech.orm.VersionedEntity;

/**
 * Example versioned entity, for testing.
 */
public class Employee implements VersionedEntity {

    private long id;

    private String name;

    private int version;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
