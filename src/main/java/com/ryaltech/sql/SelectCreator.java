package com.ryaltech.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;

/**
 * A Spring PreparedStatementCreator that you can use like a SelectBuilder.
 * Example usage is as follows:
 *
 * <pre>
 * PreparedStatementCreator psc = new SelectCreator()
 * .column("name")
 * .column("salary")
 * .from("emp")
 * .whereEquals("id", employeeId)
 * .and("salary > :limit")
 * .setParameter("limit", 100000);
 *
 * new JdbcTemplate(dataSource).query(psc, new RowMapper() { ... });
 * </pre>
 *
*
 *
 */


public class SelectCreator implements PreparedStatementCreator {

    private SelectBuilder builder = new SelectBuilder();

    private ParameterizedPreparedStatementCreator ppsc = new ParameterizedPreparedStatementCreator();

    private int paramIndex;

    public SelectCreator() {
    }


    public SelectCreator and(String expr) {
        builder.and(expr);
        return this;
    }

    public SelectCreator column(String name) {
        builder.column(name);
        return this;
    }

    public SelectCreator column(String name, boolean groupBy) {
        builder.column(name, groupBy);
        return this;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
        ppsc.setSql(builder.toString());
        return ppsc.createPreparedStatement(conn);
    }

    public SelectCreator forUpdate() {
        builder.forUpdate();
        return this;
    }

    public SelectCreator from(String table) {
        builder.from(table);
        return this;
    }

    public SelectCreator groupBy(String expr) {
        builder.groupBy(expr);
        return this;
    }

    public SelectCreator having(String expr) {
        builder.having(expr);
        return this;
    }

    public SelectCreator leftJoin(String join) {
        builder.leftJoin(join);
        return this;
    }

    public SelectCreator noWait() {
        builder.noWait();
        return this;
    }

    public SelectCreator orderBy(String name) {
        builder.orderBy(name);
        return this;
    }

    public SelectCreator setParameter(String name, Object value) {
        ppsc.setParameter(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(builder.toString());
        List<String> params = new ArrayList<String>(ppsc.getParameterMap().keySet());
        Collections.sort(params);
        for (String s : params) {
            sb.append(", ").append(s).append("=").append(ppsc.getParameterMap().get(s));
        }
        return sb.toString();
    }

    public SelectCreator where(String expr) {
        builder.where(expr);
        return this;
    }

    public SelectCreator whereEquals(String expr, Object value) {

        String param = "param" + paramIndex;
        paramIndex++;

        builder.where(expr + " = :" + param);
        ppsc.setParameter(param, value);

        return this;
    }
    
    public SelectCreator whereMore(String expr, Object value) {

        String param = "param" + paramIndex;
        paramIndex++;

        builder.where(expr + " > :" + param);
        ppsc.setParameter(param, value);

        return this;
    }

    public SelectCreator whereLess(String expr, Object value) {

        String param = "param" + paramIndex;
        paramIndex++;

        builder.where(expr + " < :" + param);
        ppsc.setParameter(param, value);

        return this;
    }

}
