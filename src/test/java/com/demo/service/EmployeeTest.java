package com.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Employee} domain model.
 */
class EmployeeTest {

    @Test
    @DisplayName("accessors expose the values set through the setters")
    void accessorsReflectSetters() {
        Employee employee = new Employee(1, "Alice", "Engineering", 95000.0);

        employee.setId(2);
        employee.setName("Bob");
        employee.setDepartment("Finance");
        employee.setSalary(82000.0);

        assertEquals(2, employee.getId());
        assertEquals("Bob", employee.getName());
        assertEquals("Finance", employee.getDepartment());
        assertEquals(82000.0, employee.getSalary());
    }

    @Test
    @DisplayName("equals and hashCode are consistent for equal employees")
    void equalsAndHashCodeAreConsistent() {
        Employee first = new Employee(1, "Alice", "Engineering", 95000.0);
        Employee second = new Employee(1, "Alice", "Engineering", 95000.0);
        Employee different = new Employee(2, "Bob", "Finance", 82000.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    @DisplayName("toString includes the employee fields")
    void toStringIncludesFields() {
        Employee employee = new Employee(1, "Alice", "Engineering", 95000.0);

        assertTrue(employee.toString().contains("Alice"));
    }
}
