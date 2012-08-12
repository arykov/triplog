package com.ryaltech.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.ryaltech.sql.InsertCreator;
import com.ryaltech.sql.SelectCreator;
import com.ryaltech.sql.UpdateCreator;

/**
 * Maps particular entity object type(T) to a table. It gets built up by
 * chaining columns and id
 *
 *
 * <pre>
 *  Example:
 *  public class Employee implements VersionedEntity { *
 *      private long id;
 *      private String name;
 *      private int version;
 *      public long getId() {
 *          return id;
 *      }
 *      public String getName() {
 *          return name;
 *      }
 *      public int getVersion() {
 *          return version;
 *      }
 *      public void setId(long id) {
 *          this.id = id;
 *      }
 *      public void setName(String name) {
 *          this.name = name;
 *      }
 *      public void setVersion(int version) {
 *          this.version = version;
 *      }
 *  }
 * create table EmployeeTable (
 *     employee_id             numeric(20) primary key,
 *     version                 numeric(5),
 *     name                    varchar(255)
 * );
 *
 *
 *  Mapping will look like
 *  new Mapping<Employee>(Employee.class, "EmployeeTable")
 *     .setIdColumn("employee_id")
 *     .setVersionColumn("ver")
 *     .add("name");
 *
 * </pre>
 *
 * @author Alex Rykov
 *
 * @param <T>
 *            entity object type
 */
public class Mapping<T extends Entity> {

    /**
     * Query object returned by Mapping#createQuery.
     */
    public class Query {

        private DataSource dataSource;

        private SelectCreator select;

        private Query(DataSource dataSource) {
            this.dataSource = dataSource;
            this.select = new SelectCreator().from(getTable());
        }

        public Query forUpdate() {
            select.forUpdate();
            return this;
        }

        @SuppressWarnings("unchecked")
        public List<T> getResultList() {

            select.column(idColumn);

            if (VersionedEntity.class.isAssignableFrom(clazz)) {
                select.column(versionColumn);
            }

            for (Column column : columns) {
                select.column(column.getColumnName());
            }

            return new JdbcTemplate(dataSource).query(select, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int row) throws SQLException {

                    T result = createInstance();

                    result.setId(rs.getLong(1));

                    if (result instanceof VersionedEntity) {
                        ((VersionedEntity) result).setVersion(rs.getInt(2));
                    }

                    for (Column column : columns) {
                        column.setFieldValueFromResultSet(result, rs, column.getColumnName());
                    }

                    return result;
                }

            });

        }

        /**
         * Returns a single result from the query.
         *
         * @throws SingleResultException
         *             if the query returned no rows or more than one row
         */
        public T getSingleResult() throws SingleResultException {
            List<T> results = getResultList();
            if (results.size() == 1) {
                return results.get(0);
            } else {
                throw new SingleResultException(results.size(), select);
            }
        }

        /**
         * Returns a single result from the query. If now matching records were
         * found, returns null.
         *
         * @throws SingleResultException
         *             if the query returned more than one row
         */
        public T getSingleResultOrNull() throws SingleResultException {
            List<T> results = getResultList();
            if (results.size() == 1) {
                return results.get(0);
            } else if (results.size() == 0) {
                return null;
            } else {
                throw new SingleResultException(results.size(), select);
            }
        }

        public Query noWait() {
            select.noWait();
            return this;
        }

        public Query setParameter(String name, Object value) {
            select.setParameter(name, value);
            return this;
        }

        public Query where(String expr) {
            select.where(expr);
            return this;
        }

        public Query where(String expr, Object value) {
            select.where(expr, value);
            return this;
        }

    }

    public static final long NULL_ID = 0;

    private Class<T> clazz;

    private String table;

    private String idColumn = "id";

    private IdGenerator idGenerator = new DefaultIdGenerator();

    private String versionColumn = "version";

    private List<Column> columns = new ArrayList<Column>();

    public Mapping(Class<T> clazz, String table) {
        super();
        this.clazz = clazz;
        this.table = table;
    }

    public Mapping<T> add(Column column) {
        columns.add(column);
        return this;
    }

    public Mapping<T> add(String name) {
        add(new Column(name));
        return this;
    }

    public Mapping<T> add(String fieldName, String columnName) {
        add(new Column(fieldName, columnName));
        return this;
    }

    public Mapping<T> add(String fieldName, String columnName, Converter<?> converter) {
        add(new Column(fieldName, columnName, converter));
        return this;
    }

    /**
     * Creates instance of the entity class
     *
     * @return instance of entity class
     */
    protected T createInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Query createQuery(DataSource dataSource) {
        return new Query(dataSource);
    }

    /**
     * Find entity by id
     *
     * @param dataSource
     * @param id
     * @return
     * @throws RowNotFoundException
     */
    public T findById(DataSource dataSource, long id) throws RowNotFoundException {

        List<T> result = createQuery(dataSource).where(idColumn+"=", id).getResultList();

        if (result.size() == 0) {
            throw new RowNotFoundException(table, id);
        } else {
            return result.get(0);
        }
    }

    public String getIdColumn() {
        return idColumn;
    }

    public String getTable() {
        return table;
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    /**
     * Insert entity object. Its "id" field gets populated in the process.
     *
     * @param dataSource
     *            datasource pointing to the schema with the table
     * @param entity
     *            entity
     * @return
     */
    public T insert(DataSource dataSource, T entity) {

        if (entity.getId() != NULL_ID) {
            throw new RuntimeException(String.format("Tried to insert object of type %s with existing id %d", entity
                    .getClass().getSimpleName(), entity.getId()));
        }

        long id = idGenerator.generate(dataSource, table);

        InsertCreator insert = new InsertCreator(table);

        insert.setValue(idColumn, id);

        if (entity instanceof VersionedEntity) {
            insert.setValue(versionColumn, 0);
        }

        for (Column column : columns) {
            insert.setValue(column.getColumnName(), column.getFieldValueAsColumn(entity));
        }

        new JdbcTemplate(dataSource).update(insert);

        entity.setId(id);

        return entity;
    }

    public Mapping<T> setIdColumn(String idColumn) {
        this.idColumn = idColumn;
        return this;
    }

    public Mapping<T> setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    public Mapping<T> setVersionColumn(String versionColumn) {
        this.versionColumn = versionColumn;
        return this;
    }

    /**
     * Updates value of entity in the table
     *
     * @param dataSource
     * @param entity
     * @return
     */
    public T update(DataSource dataSource, T entity) throws RowNotFoundException, OptimisticLockException {

        if (entity.getId() == NULL_ID) {
            throw new RuntimeException(String.format("Tried to insert object of type %s with null ID", entity
                    .getClass().getSimpleName()));
        }

        UpdateCreator update = new UpdateCreator(table);

        update.whereEquals(idColumn, entity.getId());

        if (entity instanceof VersionedEntity) {
            update.set(versionColumn + " = " + versionColumn + " + 1");
            update.whereEquals(versionColumn, ((VersionedEntity) entity).getVersion());
        }

        for (Column column : columns) {
            update.setValue(column.getColumnName(), column.getFieldValueAsColumn(entity));
        }

        int rows = new JdbcTemplate(dataSource).update(update);

        if (rows == 1) {

            if (entity instanceof VersionedEntity) {
                VersionedEntity ve = (VersionedEntity) entity;
                ve.setVersion(ve.getVersion() + 1);
            }

            return entity;

        } else if (rows > 1) {

            throw new RuntimeException(
                    String
                            .format(
                                    "Updating table %s with id %s updated %d rows. There must be a mapping problem. Is column %s really the primary key?",
                                    table, entity.getId(), rows, idColumn));

        } else {

            //
            // Updated zero rows. This could be because our ID is wrong, or
            // because our object is out-of date. Let's try querying just by ID.
            //

            SelectCreator selectById = new SelectCreator().column("count(*)").from(table).where(idColumn+"=",
                    entity.getId());

            rows = (Integer) new JdbcTemplate(dataSource).query(selectById, new ResultSetExtractor() {
                @Override
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    rs.next();
                    return rs.getInt(1);
                }
            });

            if (rows == 0) {
                throw new RowNotFoundException(table, entity.getId());
            } else {
                throw new OptimisticLockException(table, entity.getId());
            }
        }
    }

}
