package com.ryaltech.sql;

import java.util.List;

public abstract class AbstractSqlBuilder {

    protected void appendList(StringBuilder sql, List<String> list, String init, String sep) {

        boolean first = true;

        for (String s : list) {
            if (first) {
                sql.append(init);
            } else {
                sql.append(sep);
            }
            sql.append(s);
            first = false;
        }
    }

}
