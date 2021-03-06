package com.ryaltech.sql;

import java.util.ArrayList;
import java.util.List;

public class InsertBuilder extends AbstractSqlBuilder {

    private String table;

    private List<String> columns = new ArrayList<String>();

    private List<String> values = new ArrayList<String>();

    public InsertBuilder(String table) {
        this.table = table;
    }

    /**
     * Inserts a column name, value pair into the SQL.
     * 
     * @param column
     *            Name of the table column.
     * @param value
     *            Value to substitute in. InsertBuilder does *no* interpretation
     *            of this. If you want a string constant inserted, you must
     *            provide the single quotes and escape the internal quotes. It
     *            is more common to use a question mark or a token in the style
     *            of {@link ParameterizedPreparedStatementCreator}, e.g. ":foo".
     */
    public InsertBuilder set(String column, String value) {
        columns.add(column);
        values.add(value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder("insert into ").append(table).append(" (");
        appendList(sql, columns, "", ", ");
        sql.append(") values (");
        appendList(sql, values, "", ", ");
        sql.append(")");
        return sql.toString();
    }
}
