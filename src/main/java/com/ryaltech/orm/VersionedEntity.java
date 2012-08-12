package com.ryaltech.orm;

/**
 * Interface all entities to allow versioning and optimistic locking have to
 * implement
 *
 * @author Alex Rykov
 *
 */
public interface VersionedEntity extends Entity {

    public int getVersion();

    public void setVersion(int version);
}
