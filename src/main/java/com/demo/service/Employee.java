package com.demo.service;

import java.util.Objects;

/**
 * Represents an employee within the organization.
 *
 * <p>An {@code Employee} is a simple, immutable-friendly data carrier holding
 * identity and basic profile information such as name, department and salary.
 * The class follows standard JavaBean conventions and overrides
 * {@link #equals(Object)}, {@link #hashCode()} and {@link #toString()} so that
 * instances behave correctly inside collections.</p>
 */
public class Employee {

    private int id;
    private String name;
    private String department;
    private double salary;

    /**
     * Creates a fully populated {@code Employee}.
     *
     * @param id         the unique identifier of the employee
     * @param name       the full name of the employee
     * @param department the department the employee belongs to
     * @param salary     the employee's salary
     */
    public Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    /**
     * Returns the unique identifier of the employee.
     *
     * @return the employee id
     */
    public int getId() {
        return id;
    }

    /**
     * Updates the unique identifier of the employee.
     *
     * @param id the new employee id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the full name of the employee.
     *
     * @return the employee name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the full name of the employee.
     *
     * @param name the new employee name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the department the employee belongs to.
     *
     * @return the employee department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Updates the department the employee belongs to.
     *
     * @param department the new employee department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Returns the employee's salary.
     *
     * @return the employee salary
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Updates the employee's salary.
     *
     * @param salary the new employee salary
     */
    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Employee employee = (Employee) o;
        return id == employee.id
                && Double.compare(employee.salary, salary) == 0
                && Objects.equals(name, employee.name)
                && Objects.equals(department, employee.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, department, salary);
    }

    @Override
    public String toString() {
        return "Employee{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", department='" + department + '\''
                + ", salary=" + salary
                + '}';
    }
}
