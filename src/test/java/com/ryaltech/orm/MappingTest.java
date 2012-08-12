package com.ryaltech.orm;

import static org.easymock.EasyMock.expect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.ryaltech.orm.IdGenerator;
import com.ryaltech.orm.Mapping;
import com.ryaltech.orm.OptimisticLockException;
import com.ryaltech.orm.RowNotFoundException;

public class MappingTest extends TestCase {

    private IMocksControl mocks;

    private DataSource dataSource;

    private Connection connection;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    private Mapping<Employee> employeeMapping;

    private LogEntryMapping logEntryMapping;

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        mocks = EasyMock.createControl();
        dataSource = mocks.createMock(DataSource.class);
        connection = mocks.createMock(Connection.class);
        preparedStatement = mocks.createMock(PreparedStatement.class);
        resultSet = mocks.createMock(ResultSet.class);

        expect(dataSource.getConnection()).andStubReturn(connection);

        IdGenerator idGenerator = new DummyIdGenerator();
        employeeMapping = new Mapping<Employee>(Employee.class, "Employee").setIdColumn("employee_id").setVersionColumn("ver").add("name");
        employeeMapping.setIdGenerator(idGenerator);
        logEntryMapping = new LogEntryMapping();
        logEntryMapping.setIdGenerator(idGenerator);

    }

    public void testFindByIdNonVersioned() throws Exception {

        expect(connection.prepareStatement("select entry_id, message_col from LogEntry where entry_id = ?")).andReturn(
                preparedStatement);
        preparedStatement.setObject(1, Long.valueOf(1));
        expect(preparedStatement.executeQuery()).andReturn(resultSet);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        expect(resultSet.next()).andReturn(true);
        expect(resultSet.getLong(1)).andReturn(1L);
        expect(resultSet.getObject("message_col")).andReturn("WTF?");
        expect(resultSet.next()).andReturn(false);
        resultSet.close();
        preparedStatement.close();
        connection.close();

        mocks.replay();

        LogEntry entry = logEntryMapping.findById(dataSource, 1);

        assertEquals(1, entry.getId());
        assertEquals("WTF?", entry.getMessage());

    }

    public void testFindByIdVersioned() throws Exception {

        expect(connection.prepareStatement("select employee_id, ver, name from Employee where employee_id = ?")).andReturn(
                preparedStatement);
        preparedStatement.setObject(1, Long.valueOf(1));
        expect(preparedStatement.executeQuery()).andReturn(resultSet);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        expect(resultSet.next()).andReturn(true);
        expect(resultSet.getLong(1)).andReturn(1L);
        expect(resultSet.getInt(2)).andReturn(2);
        expect(resultSet.getObject("name")).andReturn("Bob");
        expect(resultSet.next()).andReturn(false);
        resultSet.close();
        preparedStatement.close();
        connection.close();

        mocks.replay();

        Employee employee = employeeMapping.findById(dataSource, 1);

        assertEquals(1, employee.getId());
        assertEquals(2, employee.getVersion());
        assertEquals("Bob", employee.getName());

    }

    public void testInsertNonVersioned() throws Exception {

        expect(connection.prepareStatement("insert into LogEntry (entry_id, message_col) values (?, ?)")).andReturn(
                preparedStatement);
        preparedStatement.setObject(1, Long.valueOf(1));
        preparedStatement.setObject(2, "WTF?");
        expect(preparedStatement.executeUpdate()).andReturn(1);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        mocks.replay();

        LogEntry entry = new LogEntry();
        entry.setMessage("WTF?");

        entry = logEntryMapping.insert(dataSource, entry);

        assertEquals(1, entry.getId());

        try {
            logEntryMapping.insert(dataSource, entry);
            assertTrue("Expected error", false);
        } catch (RuntimeException ex) {
        }

        mocks.verify();
    }

    public void testInsertVersioned() throws Exception {

        expect(connection.prepareStatement("insert into Employee (employee_id, ver, name) values (?, ?, ?)")).andReturn(
                preparedStatement);
        preparedStatement.setObject(1, Long.valueOf(1));
        preparedStatement.setObject(2, Integer.valueOf(0));
        preparedStatement.setObject(3, "Bob");
        expect(preparedStatement.executeUpdate()).andReturn(1);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        mocks.replay();

        Employee employee = new Employee();
        employee.setName("Bob");

        employee = employeeMapping.insert(dataSource, employee);

        assertEquals(1, employee.getId());

        try {
            employeeMapping.insert(dataSource, employee);
            assertTrue("Expected error", false);
        } catch (RuntimeException ex) {
        }

        mocks.verify();
    }

    public void testOptimisticLock() throws Exception {

        expect(connection.prepareStatement("update Employee set ver = ver + 1, name = ? where employee_id = ? and ver = ?"))
                .andReturn(preparedStatement);
        preparedStatement.setObject(1, "Bob");
        preparedStatement.setObject(2, Long.valueOf(1));
        preparedStatement.setObject(3, Integer.valueOf(0));
        expect(preparedStatement.executeUpdate()).andReturn(0);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        expect(connection.prepareStatement("select count(*) from Employee where employee_id = ?")).andReturn(preparedStatement);
        preparedStatement.setObject(1, Long.valueOf(1));
        expect(preparedStatement.executeQuery()).andReturn(resultSet);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        expect(resultSet.next()).andReturn(true);
        expect(resultSet.getInt(1)).andReturn(1);
        resultSet.close();
        preparedStatement.close();
        connection.close();

        mocks.replay();

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Bob");
        assertEquals(0, employee.getVersion());

        try {
            employee = employeeMapping.update(dataSource, employee);
            assertTrue("Expected OptimisticLockException", false);
        } catch (OptimisticLockException e) {
        }

        mocks.verify();
    }

    public void testRowNotFound() throws Exception {

        expect(connection.prepareStatement("update Employee set ver = ver + 1, name = ? where employee_id = ? and ver = ?"))
                .andReturn(preparedStatement);
        preparedStatement.setObject(1, "Bob");
        preparedStatement.setObject(2, Long.valueOf(1));
        preparedStatement.setObject(3, Integer.valueOf(0));
        expect(preparedStatement.executeUpdate()).andReturn(0);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        expect(connection.prepareStatement("select count(*) from Employee where employee_id = ?")).andReturn(preparedStatement);
        preparedStatement.setObject(1, Long.valueOf(1));
        expect(preparedStatement.executeQuery()).andReturn(resultSet);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        expect(resultSet.next()).andReturn(true);
        expect(resultSet.getInt(1)).andReturn(0);
        resultSet.close();
        preparedStatement.close();
        connection.close();

        mocks.replay();

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Bob");
        assertEquals(0, employee.getVersion());

        try {
            employee = employeeMapping.update(dataSource, employee);
            assertTrue("Expected RowNotFoundException", false);
        } catch (RowNotFoundException e) {
        }

        mocks.verify();
    }

    public void testUpdateNonVersioned() throws Exception {

        expect(connection.prepareStatement("update LogEntry set message_col = ? where entry_id = ?")).andReturn(
                preparedStatement);
        preparedStatement.setObject(1, "Foo");
        preparedStatement.setObject(2, Long.valueOf(1));
        expect(preparedStatement.executeUpdate()).andReturn(1);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        mocks.replay();

        LogEntry entry = new LogEntry();
        entry.setMessage("Foo");

        try {
            logEntryMapping.update(dataSource, entry);
            assertTrue("Expected error", false);
        } catch (RuntimeException ex) {
        }

        entry.setId(1);

        entry = logEntryMapping.update(dataSource, entry);

        assertEquals(1, entry.getId());

        mocks.verify();
    }

    public void testUpdateVersionedSuccess() throws Exception {

        expect(connection.prepareStatement("update Employee set ver = ver + 1, name = ? where employee_id = ? and ver = ?"))
                .andReturn(preparedStatement);
        preparedStatement.setObject(1, "Bob");
        preparedStatement.setObject(2, Long.valueOf(1));
        preparedStatement.setObject(3, Integer.valueOf(0));
        expect(preparedStatement.executeUpdate()).andReturn(1);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        mocks.replay();

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Bob");
        assertEquals(0, employee.getVersion());

        employee = employeeMapping.update(dataSource, employee);

        assertEquals(1, employee.getId());
        assertEquals(1, employee.getVersion());

        mocks.verify();
    }

    public void testUpdateVersionedTooMany() throws Exception {

        expect(connection.prepareStatement("update Employee set ver = ver + 1, name = ? where employee_id = ? and ver = ?"))
                .andReturn(preparedStatement);
        preparedStatement.setObject(1, "Bob");
        preparedStatement.setObject(2, Long.valueOf(1));
        preparedStatement.setObject(3, Integer.valueOf(0));
        expect(preparedStatement.executeUpdate()).andReturn(2);
        expect(preparedStatement.getWarnings()).andStubReturn(null);
        preparedStatement.close();
        connection.close();

        mocks.replay();

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Bob");
        assertEquals(0, employee.getVersion());

        try {
            employee = employeeMapping.update(dataSource, employee);
            assertTrue("Expected RuntimeException", false);
        } catch (RuntimeException e) {
        }

        mocks.verify();
    }

}
