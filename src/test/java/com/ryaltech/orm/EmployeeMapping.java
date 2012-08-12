package com.ryaltech.orm;

import com.ryaltech.orm.Mapping;

public class EmployeeMapping extends Mapping<Employee> {

    public EmployeeMapping() {
        super(Employee.class, "Employee");
        setVersionColumn("ver");
        add("name");
    }

}
