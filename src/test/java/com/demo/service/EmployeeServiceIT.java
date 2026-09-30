package com.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for {@link EmployeeService}.
 *
 * <p>Named with the {@code *IT} suffix so the Failsafe plugin runs them during
 * the {@code integration-test} phase, separately from the Surefire unit tests.</p>
 */
class EmployeeServiceIT {

    @Test
    @DisplayName("full lifecycle: add, find, then remove works end to end")
    void fullLifecycleAddFindRemoveWorksEndToEnd() {
        EmployeeService service = new EmployeeService();

        service.addEmployee(new Employee(1, "Asha", "Engineering", 90000.0));

        Optional<Employee> found = service.findEmployeeById(1);
        assertTrue(found.isPresent());
        assertEquals("Asha", found.get().getName());

        assertTrue(service.removeEmployee(1));
        assertFalse(service.findEmployeeById(1).isPresent());
    }
}
