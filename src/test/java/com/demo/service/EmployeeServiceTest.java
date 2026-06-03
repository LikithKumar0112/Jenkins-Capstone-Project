package com.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EmployeeService}.
 */
class EmployeeServiceTest {

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService();
    }

    @Test
    @DisplayName("addEmployee stores a valid employee")
    void addEmployeeStoresValidEmployee() {
        Employee employee = new Employee(1, "Alice Johnson", "Engineering", 95000.0);

        service.addEmployee(employee);

        List<Employee> all = service.getAllEmployees();
        assertEquals(1, all.size());
        assertEquals(employee, all.get(0));
    }

    @Test
    @DisplayName("addEmployee rejects a null employee")
    void addEmployeeRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.addEmployee(null));
    }

    @Test
    @DisplayName("addEmployee rejects a non-positive id")
    void addEmployeeRejectsNonPositiveId() {
        Employee employee = new Employee(0, "Invalid", "Engineering", 1000.0);

        assertThrows(IllegalArgumentException.class, () -> service.addEmployee(employee));
    }

    @Test
    @DisplayName("addEmployee rejects a duplicate id")
    void addEmployeeRejectsDuplicateId() {
        service.addEmployee(new Employee(1, "Alice", "Engineering", 95000.0));

        Employee duplicate = new Employee(1, "Bob", "Finance", 82000.0);
        assertThrows(IllegalArgumentException.class, () -> service.addEmployee(duplicate));
    }

    @Test
    @DisplayName("removeEmployee removes an existing employee")
    void removeEmployeeRemovesExisting() {
        service.addEmployee(new Employee(1, "Alice", "Engineering", 95000.0));

        boolean removed = service.removeEmployee(1);

        assertTrue(removed);
        assertTrue(service.getAllEmployees().isEmpty());
    }

    @Test
    @DisplayName("removeEmployee returns false for an unknown id")
    void removeEmployeeReturnsFalseForUnknownId() {
        boolean removed = service.removeEmployee(99);

        assertFalse(removed);
    }

    @Test
    @DisplayName("removeEmployee rejects a non-positive id")
    void removeEmployeeRejectsNonPositiveId() {
        assertThrows(IllegalArgumentException.class, () -> service.removeEmployee(-5));
    }

    @Test
    @DisplayName("findEmployeeById returns the matching employee")
    void findEmployeeByIdReturnsMatch() {
        Employee employee = new Employee(2, "Bob Smith", "Finance", 82000.0);
        service.addEmployee(employee);

        Optional<Employee> found = service.findEmployeeById(2);

        assertTrue(found.isPresent());
        assertEquals(employee, found.get());
    }

    @Test
    @DisplayName("findEmployeeById returns empty when not found")
    void findEmployeeByIdReturnsEmptyWhenMissing() {
        Optional<Employee> found = service.findEmployeeById(42);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findEmployeeById rejects a non-positive id")
    void findEmployeeByIdRejectsNonPositiveId() {
        assertThrows(IllegalArgumentException.class, () -> service.findEmployeeById(0));
    }

    @Test
    @DisplayName("getAllEmployees returns every stored employee")
    void getAllEmployeesReturnsAll() {
        service.addEmployee(new Employee(1, "Alice", "Engineering", 95000.0));
        service.addEmployee(new Employee(2, "Bob", "Finance", 82000.0));

        List<Employee> all = service.getAllEmployees();

        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("getAllEmployees returns an unmodifiable list")
    void getAllEmployeesReturnsUnmodifiableList() {
        service.addEmployee(new Employee(1, "Alice", "Engineering", 95000.0));

        List<Employee> all = service.getAllEmployees();
        Employee extra = new Employee(2, "Bob", "Finance", 82000.0);

        assertThrows(UnsupportedOperationException.class, () -> all.add(extra));
    }
}
