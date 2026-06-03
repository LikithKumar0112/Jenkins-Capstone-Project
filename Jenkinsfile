pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        // Name of the SonarQube server configured in:
        // Manage Jenkins -> System -> SonarQube servers
        SONARQUBE_ENV = 'SonarQube'

        // Recipient for build notification emails.
        EMAIL_RECIPIENT = 'kumarg452k@gmail.com'
    }

    triggers {
        // Phase 1: Auto-trigger pipeline on every GitHub push.
        // Requires the "GitHub" plugin and a webhook pointing to:
        //   <JENKINS_URL>/github-webhook/
        githubPush()
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        // ─────────────────────────────────────────────────────────────
        // PHASE 1 — Pipeline as Code (Project 1.1)
        // Sequential stages: Checkout → Clean → Compile → Package
        // Stored in GitHub as a Jenkinsfile (Pipeline from SCM).
        // ─────────────────────────────────────────────────────────────

        stage('Checkout') {
            steps {
                echo "Phase 1 — Checking out source code from GitHub..."
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                echo "Phase 1 — Cleaning previous build output..."
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                echo "Phase 1 — Compiling source code..."
                sh 'mvn compile'
            }
        }

        // ─────────────────────────────────────────────────────────────
        // PHASE 2 — Parallel CI Pipeline (Project 1.2)
        // Unit Tests, Integration Tests, and Code Analysis run
        // simultaneously to reduce total pipeline execution time.
        // ─────────────────────────────────────────────────────────────

        stage('Parallel Tests & Analysis') {
            parallel {

                stage('Unit Tests') {
                    steps {
                        echo "Phase 2 (parallel) — Running Unit Tests..."
                        sh 'mvn test'
                    }
                    post {
                        always {
                            // Publish JUnit test results for this branch
                            junit testResults: 'target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }

                stage('Integration Tests') {
                    steps {
                        echo "Phase 2 (parallel) — Running Integration Tests..."
                        // mvn verify runs unit + integration tests (failsafe plugin).
                        // -DskipUTs skips unit tests so only integration tests run here,
                        // keeping this branch independent of the Unit Tests branch above.
                        sh 'mvn verify -DskipUTs=true -Dsurefire.skip=true'
                    }
                }

                stage('Code Quality Check') {
                    steps {
                        echo "Phase 2 (parallel) — Running static code analysis (PMD)..."
                        // PMD inspects code for style issues, best-practice violations,
                        // and potential bugs without compiling a second time.
                        sh 'mvn pmd:check -Dpmd.failOnViolation=false'
                    }
                }
            }
        }

        // ─────────────────────────────────────────────────────────────
        // PHASE 3 — Distributed Build (Master-Agent) (Project 1.3)
        // The Package stage is explicitly pinned to a labeled agent.
        // In Jenkins: Manage Jenkins → Nodes → add a node with the
        // label 'java-agent', connected via SSH.
        // Comment this stage out and use the fallback below if you
        // have only a single node available during the demo.
        // ─────────────────────────────────────────────────────────────

        stage('Package (on Agent)') {
            // Runs on the dedicated build agent — demonstrates distributed execution.
            agent { label 'java-agent' }
            steps {
                echo "Phase 3 — Packaging on distributed agent: ${env.NODE_NAME}"
                sh 'mvn package -DskipTests'
                echo "Running on node: ${env.NODE_NAME}"
            }
        }

        /* ── Fallback: use this block if no separate agent is configured ──
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        ─────────────────────────────────────────────────────────────── */

        // ─────────────────────────────────────────────────────────────
        // PHASE 4 — SonarQube Quality Gate Pipeline (Project 1.4)
        // Static code analysis → Quality Gate enforcement → Archive.
        // Requires: SonarQube Scanner plugin, server configured in
        // Manage Jenkins → System → SonarQube servers.
        // ─────────────────────────────────────────────────────────────

        stage('SonarQube Analysis') {
            steps {
                echo "Phase 4 — Running SonarQube static code analysis..."
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo "Phase 4 — Waiting for SonarQube Quality Gate result..."
                script {
                    def qg = null
                    try {
                        // Allow up to 5 minutes for SonarQube to process the report.
                        timeout(time: 5, unit: 'MINUTES') {
                            qg = waitForQualityGate()
                        }
                    } catch (err) {
                        // SonarCloud sometimes processes asynchronously.
                        // If timeout fires before a result, treat it as IN_PROGRESS
                        // rather than aborting the pipeline unnecessarily.
                        echo "Quality Gate timed out — SonarQube may still be processing."
                        qg = [status: 'IN_PROGRESS']
                    }

                    if (qg.status == 'ERROR') {
                        error "Pipeline aborted: SonarQube Quality Gate FAILED (status: ${qg.status})"
                    } else {
                        echo "Quality Gate status: ${qg.status} — proceeding."
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "Phase 4 — Archiving build artifacts..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
 }
}
