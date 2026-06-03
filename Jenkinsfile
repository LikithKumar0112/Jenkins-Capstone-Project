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
        // Auto-trigger the pipeline when code is pushed to GitHub.
        // Requires the "GitHub" plugin and a GitHub webhook pointing to:
        //   <JENKINS_URL>/github-webhook/
        githubPush()
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
            emailext(
                to: "${EMAIL_RECIPIENT}",
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """\
The pipeline completed successfully.

Job:    ${env.JOB_NAME}
Build:  #${env.BUILD_NUMBER}
Status: SUCCESS
URL:    ${env.BUILD_URL}
"""
            )
            // --- Optional Slack notification (requires the "Slack Notification" plugin) ---
            // slackSend(
            //     channel: '#ci-builds',
            //     color: 'good',
            //     message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER} (${env.BUILD_URL})"
            // )
        }
        failure {
            echo 'Pipeline failed. Please review the logs.'
            emailext(
                to: "${EMAIL_RECIPIENT}",
                subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """\
The pipeline failed. Please review the attached log.

Job:    ${env.JOB_NAME}
Build:  #${env.BUILD_NUMBER}
Status: FAILURE
URL:    ${env.BUILD_URL}
""",
                attachLog: true
            )
            // --- Optional Slack notification (requires the "Slack Notification" plugin) ---
            // slackSend(
            //     channel: '#ci-builds',
            //     color: 'danger',
            //     message: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER} (${env.BUILD_URL})"
            // )
        }
        always {
            echo 'Pipeline execution finished.'
        }
    }
}
