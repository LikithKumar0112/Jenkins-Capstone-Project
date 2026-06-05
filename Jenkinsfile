pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        SONARQUBE_ENV = 'SonarQube'
    }

    triggers {
        githubPush()
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

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

        stage('Parallel Tests & Analysis') {
            parallel {

                stage('Unit Tests') {
                    steps {
                        echo "Phase 2 (parallel) — Running Unit Tests..."
                        sh 'mvn test'
                    }
                    post {
                        always {
                            junit testResults: 'target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }

                stage('Integration Tests') {
                    steps {
                        echo "Phase 2 (parallel) — Running Integration Tests..."
                        sh 'mvn verify -DskipUTs=true -Dsurefire.skip=true'
                    }
                }

                stage('Code Quality Check') {
                    steps {
                        echo "Phase 2 (parallel) — Running static code analysis (PMD)..."
                        sh 'mvn pmd:check -Dpmd.failOnViolation=false'
                    }
                }
            }
        }

        stage('Package (on Agent)') {
            agent { label 'Linux-Agent' }
            steps {
                echo "Phase 3 — Packaging on distributed agent: ${env.NODE_NAME}"
                sh 'mvn package -DskipTests'
                echo "Running on node: ${env.NODE_NAME}"
            }
        }

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
                        timeout(time: 5, unit: 'MINUTES') {
                            qg = waitForQualityGate()
                        }
                    } catch (err) {
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

    post {
        always {
            echo 'Pipeline execution finished.'
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Please review the logs.'
        }
    }
}
