package com.demo.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the Employee Management demonstration application.
 *
 * <p>The application wires up an {@link EmployeeService}, populates it with a
 * few sample employees and exercises the available operations: listing,
 * searching and removing employees. It is intended to showcase a complete
 * Maven, JUnit, Jenkins and SonarQube workflow rather than to be a fully
 * featured product.</p>
 */
public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
        // Utility entry-point class; not meant to be instantiated.
    }

    /**
     * Runs the demonstration workflow.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();

        service.addEmployee(new Employee(1, "Alice Johnson", "Engineering", 95000.0));
        service.addEmployee(new Employee(2, "Bob Smith", "Finance", 82000.0));
        service.addEmployee(new Employee(3, "Carla Reyes", "Engineering", 105000.0));

        LOGGER.info("Initial employee list:");
        service.getAllEmployees().forEach(employee -> LOGGER.info("{}", employee));

        int searchId = 2;
        Optional<Employee> found = service.findEmployeeById(searchId);
        found.ifPresentOrElse(
                employee -> LOGGER.info("Found employee for id {}: {}", searchId, employee),
                () -> LOGGER.warn("No employee found for id {}", searchId));

        int removeId = 1;
        boolean removed = service.removeEmployee(removeId);
        LOGGER.info("Removal of employee id {} succeeded: {}", removeId, removed);

        LOGGER.info("Final employee list:");
        service.getAllEmployees().forEach(employee -> LOGGER.info("{}", employee));
    }
}
