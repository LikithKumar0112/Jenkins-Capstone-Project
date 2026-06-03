# Employee Management CI Demo

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![JUnit5](https://img.shields.io/badge/Tests-JUnit%205-25A162?logo=junit5&logoColor=white)
![Jenkins](https://img.shields.io/badge/CI-Jenkins-D24939?logo=jenkins&logoColor=white)
![SonarQube](https://img.shields.io/badge/Quality-SonarQube-4E9BCD?logo=sonarqube&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

A production-ready **Java 17 + Maven** project that demonstrates a complete
DevOps continuous-integration workflow: building and testing a small Employee
Management Service, running it through a **Jenkins** declarative pipeline, and
analyzing code quality with **SonarQube**.

This repository is intended as a DevOps portfolio project showcasing Java
development, Maven build automation, JUnit testing, Jenkins CI, SonarQube code
analysis, and GitHub repository management.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Build Instructions](#build-instructions)
- [Run Instructions](#run-instructions)
- [Jenkins Pipeline Flow](#jenkins-pipeline-flow)
- [CI Notifications](#ci-notifications)
- [SonarQube Integration](#sonarqube-integration)
- [Pushing to GitHub](#pushing-to-github)
- [Future Enhancements](#future-enhancements)
- [Quick Command Reference](#quick-command-reference)
- [License](#license)

---

## Project Overview

The application implements an in-memory **Employee Management Service**.
Each `Employee` has an `id`, `name`, `department`, and `salary`. The
`EmployeeService` exposes operations to add, remove, search, and list
employees, with input validation and SLF4J logging throughout.

The accompanying `App` class wires the service together and demonstrates a
realistic workflow: adding employees, listing them, searching by id, removing
an employee, and printing the final list.

---

## Architecture

```
+-------------------+
|       App         |   Application entry point / demo workflow
+---------+---------+
          |
          v
+-------------------+
|  EmployeeService  |   Business logic: add / remove / find / list
+---------+---------+
          |
          v
+-------------------+
|     Employee      |   Domain model (id, name, department, salary)
+-------------------+
```

| Layer            | Class               | Responsibility                                   |
|------------------|---------------------|--------------------------------------------------|
| Entry point      | `App`               | Bootstraps the service and runs the demo flow.   |
| Service          | `EmployeeService`   | CRUD-style operations, validation, logging.      |
| Domain model     | `Employee`          | Data carrier with `equals`/`hashCode`/`toString`.|

All classes live under the `com.demo.service` package.

---

## Technologies Used

- **Java 17**
- **Maven** (build automation)
- **JUnit 5 (Jupiter)** (unit testing)
- **SLF4J + slf4j-simple** (logging)
- **Jenkins** (declarative CI pipeline)
- **SonarQube** (static code analysis)
- **GitHub** (source control & repository management)

---

## Getting Started

Clone the repository and move into the project directory:

```bash
git clone https://github.com/<your-username>/employee-management-ci-demo.git
cd employee-management-ci-demo
```

> Replace `<your-username>` with your GitHub username (or organization) once the
> repository has been created. See [Pushing to GitHub](#pushing-to-github).

---

## Build Instructions

Prerequisites: **JDK 17** and **Maven 3.8+** on your `PATH`.

```bash
# Clean, run tests, and package
mvn clean test package

# Or use the helper script
./scripts/build.sh
```

The build produces a runnable jar at
`target/employee-management-ci-demo-1.0.0.jar`.

---

## Run Instructions

```bash
# Run the packaged application
java -jar target/employee-management-ci-demo-1.0.0.jar

# Or use the helper script (locates the jar automatically)
./scripts/run.sh
```

You can also run directly through Maven during development:

```bash
mvn compile exec:java -Dexec.mainClass=com.demo.service.App
```

To execute only the tests:

```bash
mvn test
```

---

## Jenkins Pipeline Flow

The `Jenkinsfile` defines a **declarative** pipeline with the following stages:

1. **Checkout** – pulls the source from SCM.
2. **Clean** – `mvn clean` removes previous build output.
3. **Compile** – `mvn compile` compiles the sources.
4. **Test** – `mvn test` runs the JUnit 5 suite, generates JaCoCo coverage, and publishes results.
5. **Package** – `mvn package` builds the jar.
6. **SonarQube Analysis** – `mvn sonar:sonar` inside `withSonarQubeEnv` sends analysis to SonarQube.
7. **Quality Gate** – `waitForQualityGate abortPipeline: true` fails the build if the gate is not met.
8. **Archive Artifacts** – archives the generated `target/*.jar`.

```
Checkout -> Clean -> Compile -> Test -> Package -> SonarQube Analysis -> Quality Gate -> Archive Artifacts
```

The pipeline expects Jenkins tool installations named `Maven3` (Maven) and
`JDK17` (JDK). Adjust the names in the `tools { }` block to match your Jenkins
configuration if needed.

The `SonarQube Analysis` and `Quality Gate` stages require:

- The **SonarQube Scanner for Jenkins** plugin installed.
- A SonarQube server configured under *Manage Jenkins → System → SonarQube servers*
  whose name matches the `SONARQUBE_ENV` value in the `Jenkinsfile` (default `SonarQube`).
- A webhook from SonarQube back to Jenkins (`<JENKINS_URL>/sonarqube-webhook/`) so
  that `waitForQualityGate` receives the gate result.

The pipeline is also triggered automatically on every push via the
`triggers { githubPush() }` block. This requires the **GitHub** plugin and a
GitHub webhook on the repository pointing to:

```
<JENKINS_URL>/github-webhook/
```

---

## CI Notifications

Build results are emailed automatically from the pipeline's `post` block using
the Jenkins **Email Extension (`emailext`)** plugin:

- **On success** – a confirmation email is sent.
- **On failure** – a failure email is sent with the build log attached.

Notifications are delivered to the address defined by the `EMAIL_RECIPIENT`
environment variable in the `Jenkinsfile` (currently `kumarg452k@gmail.com`).

> **Setup required in Jenkins:** install the *Email Extension Plugin* and
> configure SMTP under *Manage Jenkins → System → Extended E-mail Notification*.

A **Slack** notification block is included (commented out) in the same `post`
section. To enable it, install the *Slack Notification* plugin, configure the
Slack workspace credentials, and uncomment the `slackSend(...)` calls.

---

## SonarQube Integration

SonarQube properties are defined in `pom.xml`:

```xml
<sonar.projectKey>employee-management-ci-demo</sonar.projectKey>
<sonar.projectName>Employee Management CI Demo</sonar.projectName>
<sonar.java.source>17</sonar.java.source>
<sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
```

Test coverage is produced by the **JaCoCo** Maven plugin during the `test`
phase (`target/site/jacoco/jacoco.xml`) and consumed by SonarQube via the
`sonar.coverage.jacoco.xmlReportPaths` property above.

Run an analysis against your SonarQube server:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<YOUR_SONAR_TOKEN>
```

This scans the codebase and reports bugs, code smells, coverage, and security
findings to the configured SonarQube instance.

---

## Pushing to GitHub

This project is not yet a git repository. To publish it to GitHub:

```bash
# 1. Initialize the repository
git init
git branch -M main

# 2. Stage and commit the project
git add .
git commit -m "Initial commit: Employee Management CI Demo"

# 3. Create an empty repository on GitHub named 'employee-management-ci-demo',
#    then link it as the remote (replace <your-username>)
git remote add origin https://github.com/<your-username>/employee-management-ci-demo.git

# 4. Push
git push -u origin main
```

The included [`.gitignore`](.gitignore) keeps build output (`target/`), IDE
folders, compiled classes, and log files out of version control.

---

## Future Enhancements

- Persist employees to a relational database (JPA / Spring Data).
- Expose a REST API with Spring Boot.
- Enforce a coverage threshold via the JaCoCo `check` goal.
- Containerize the application with Docker and deploy via Kubernetes.
- Add GitHub Actions workflows alongside the Jenkins pipeline.
- Introduce integration tests and contract tests.

---

## Quick Command Reference

```bash
# Build
mvn clean test package

# Test only
mvn test

# Run
java -jar target/employee-management-ci-demo-1.0.0.jar

# SonarQube analysis
mvn clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=<TOKEN>
```

---

## License

This project is released under the [MIT License](LICENSE). Feel free to use it
as a reference for your own DevOps portfolio.
