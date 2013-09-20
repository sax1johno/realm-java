package com.tightdb.typed;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Date;

import com.tightdb.ColumnType;
import com.tightdb.Group;
import com.tightdb.Mixed;
import com.tightdb.Table;
import com.tightdb.test.TestEmployeeTable;

public class TableTest {

    protected static final String NAME0 = "John";
    protected static final String NAME1 = "Nikolche";
    protected static final String NAME2 = "Johny";

    protected TestEmployeeTable employees;

    @BeforeMethod
    public void init() {
        // !!! Note: If any of the valueas are changed, update
        // shouldConvertToJson() 'expected' text
        Date date = new Date(1234567890);
        employees = new TestEmployeeTable();

        employees.add(NAME0, "Doe", 10000, true, new byte[] { 1, 2, 3 }, date, "extra", null);
        employees.add(NAME2, "B. Good", 10000, true, new byte[] { 1 }, date, true, null);
        employees.insert(1, NAME1, "Mihajlovski", 30000, false, new byte[] { 1 }, date, 1234, null);
        employees.add("NoName", "Test Mixed Date", 1, true, new byte[] { 1 }, date, new Date(123456789), null);
        employees.add("NoName", "Test Mixed Binary", 1, true, new byte[] { 1, 2, 3 }, date, new byte[] { 3, 2, 1 },
                null);
    }

    @AfterMethod
    public void clear() {
        employees.clear();
        assertEquals(true, employees.isEmpty());
    }
    
    
    @Test
    public void multipleTablesOfSameTypeInGroup() {
        Group group = new Group();
        
        TestEmployeeTable t0 = new TestEmployeeTable(group);
        
        assertEquals(1, group.size());
        assertEquals("TestEmployeeTable", group.getTableName(0));

        
        TestEmployeeTable t1 = new TestEmployeeTable(group, "t1");
        TestEmployeeTable t2 = new TestEmployeeTable(group, "t2");
        
        t2.add("NoName", "Test Mixed Binary", 1, true, new byte[] { 1, 2, 3 }, new Date(), new byte[] { 3, 2, 1 },null);
        
        assertEquals(3, group.size());
        
        assertEquals("t1", group.getTableName(1));
        assertEquals("t2", group.getTableName(2));
        
        
        TestEmployeeTable t2Out = new TestEmployeeTable(group, "t2");
        assertEquals("NoName", t2Out.get(0).getFirstName());
    }

    @Test
    public void shouldRetrieveRowsByIndex() {
        assertEquals(NAME0, employees.get(0).getFirstName());
        assertEquals(NAME1, employees.get(1).getFirstName());
        assertEquals(NAME2, employees.get(2).getFirstName());
    }

    @Test
    public void shouldHaveTwoWaysToReadCellValues() {
        assertEquals(NAME0, employees.get(0).getFirstName());
        assertEquals(NAME0, employees.get(0).firstName.get());
    }

    @Test
    public void shouldHaveTwoWaysToWriteCellValues() {
        employees.get(0).setFirstName("FOO");
        assertEquals("FOO", employees.get(0).getFirstName());

        employees.get(0).firstName.set("BAR");
        assertEquals("BAR", employees.get(0).getFirstName());
    }

    @Test
    public void shouldSetEntireRow() {
        Date date = new Date(1234567890);
        byte[] bytes = new byte[] { 1, 3, 5 };


        employees.get(0).set(NAME2, "Bond", 10000, true, bytes, date, new Mixed(true), null);

        assertEquals(NAME2, employees.get(0).getFirstName());
        assertEquals("Bond", employees.get(0).getLastName());
        assertEquals(10000, employees.get(0).getSalary());
        assertEquals(true, employees.get(0).getDriver());
        assertEquals(bytes, employees.get(0).getPhoto());
        assertEquals(date.getTime() / 1000, employees.get(0).getBirthdate().getTime() / 1000);
        assertEquals(new Mixed(true), employees.get(0).getExtra());
    }

    @Test
    public void shouldAllowMixedValues() throws IllegalAccessException {
        assertEquals("extra", employees.get(0).getExtra().getValue());
        assertEquals("extra", employees.get(0).getExtra().getStringValue());

        assertEquals(1234L, employees.get(1).getExtra().getValue());
        assertEquals(1234L, employees.get(1).getExtra().getLongValue());

        assertEquals(true, employees.get(2).getExtra().getValue());
        assertEquals(true, employees.get(2).getExtra().getBooleanValue());

        employees.get(1).setExtra(Mixed.mixedValue("new_value"));
        assertEquals("new_value", employees.get(1).getExtra().getValue());
        assertEquals("new_value", employees.get(1).getExtra().getStringValue());
    }

    @Test
    public void shouldOptimizeStrings() {
        // TODO: Add a lot of identical strings and test the size of the
        // database get's smaller

        employees.optimize();
    }

    @Test()
    public void shouldConvertToJson() {
        String json = employees.toJson();
        String expect = "[{\"firstName\":\"John\",\"lastName\":\"Doe\",\"salary\":10000,\"driver\":true,\"photo\":\"010203\",\"birthdate\":\"1970-01-15 06:56:07\",\"extra\":\"extra\",\"phones\":[]},{\"firstName\":\"Nikolche\",\"lastName\":\"Mihajlovski\",\"salary\":30000,\"driver\":false,\"photo\":\"01\",\"birthdate\":\"1970-01-15 06:56:07\",\"extra\":1234,\"phones\":[]},{\"firstName\":\"Johny\",\"lastName\":\"B. Good\",\"salary\":10000,\"driver\":true,\"photo\":\"01\",\"birthdate\":\"1970-01-15 06:56:07\",\"extra\":true,\"phones\":[]},{\"firstName\":\"NoName\",\"lastName\":\"Test Mixed Date\",\"salary\":1,\"driver\":true,\"photo\":\"01\",\"birthdate\":\"1970-01-15 06:56:07\",\"extra\":\"1973-11-29 21:33:09\",\"phones\":[]},{\"firstName\":\"NoName\",\"lastName\":\"Test Mixed Binary\",\"salary\":1,\"driver\":true,\"photo\":\"010203\",\"birthdate\":\"1970-01-15 06:56:07\",\"extra\":\"030201\",\"phones\":[]}]";
        assertEquals(json, expect);
    }

}
