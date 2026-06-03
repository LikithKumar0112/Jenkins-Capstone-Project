package com.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides in-memory management operations for {@link Employee} records.
 *
 * <p>This service maintains an internal {@link List} of employees and exposes
 * operations to add, remove, search and list employees. All mutating
 * operations perform input validation and emit structured log messages via
 * SLF4J.</p>
 */
public class EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    private final List<Employee> employees = new ArrayList<>();

    /**
     * Adds a new employee to the underlying store.
     *
     * @param employee the employee to add; must not be {@code null} and must
     *                 carry a positive identifier
     * @throws IllegalArgumentException if the employee is {@code null}, has a
     *                                  non-positive id, or an employee with the
     *                                  same id already exists
     */
    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee must not be null");
        }
        if (employee.getId() <= 0) {
            throw new IllegalArgumentException("Employee id must be a positive value");
        }
        if (findEmployeeById(employee.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Employee with id " + employee.getId() + " already exists");
        }
        employees.add(employee);
        LOGGER.info("Added employee with id {} and name {}", employee.getId(), employee.getName());
    }

    /**
     * Removes the employee that matches the supplied identifier.
     *
     * @param id the identifier of the employee to remove
     * @return {@code true} if an employee was removed, {@code false} otherwise
     * @throws IllegalArgumentException if the id is not positive
     */
    public boolean removeEmployee(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee id must be a positive value");
        }
        boolean removed = employees.removeIf(employee -> employee.getId() == id);
        if (removed) {
            LOGGER.info("Removed employee with id {}", id);
        } else {
            LOGGER.warn("No employee found to remove for id {}", id);
        }
        return removed;
    }

    /**
     * Finds an employee by its identifier.
     *
     * @param id the identifier to search for
     * @return an {@link Optional} containing the matching employee, or an empty
     *         {@code Optional} if none is found
     * @throws IllegalArgumentException if the id is not positive
     */
    public Optional<Employee> findEmployeeById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee id must be a positive value");
        }
        Optional<Employee> result = employees.stream()
                .filter(employee -> employee.getId() == id)
                .findFirst();
        LOGGER.debug("Lookup for employee id {} returned present={}", id, result.isPresent());
        return result;
    }

    /**
     * Returns an unmodifiable view of all managed employees.
     *
     * @return an unmodifiable list of all employees
     */
    public List<Employee> getAllEmployees() {
        LOGGER.debug("Returning all {} employees", employees.size());
        return Collections.unmodifiableList(new ArrayList<>(employees));
    }
}
