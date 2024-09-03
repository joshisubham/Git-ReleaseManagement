pipeline {
    agent any

    tools {
        jdk "openjdk-17"
        maven "Maven3"
    }

    environment {
        POM_VERSION = readMavenPom().getVersion()
        BRANCH_NAME = ""
        RELEASE = false
    }

    stages {
        stage('Fetch code') {
            steps {
                script {
                    BRANCH_NAME = env.GIT_BRANCH?.replaceAll('origin/', '') ?: sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    echo "Building branch: ${BRANCH_NAME}"
                }
            }
        }
        stage('Determine Version Strategy') {
            when {
                expression {
                    BRANCH_NAME.startsWith('release/') || BRANCH_NAME.startsWith('develop')
                }
            }
            steps {
                script {
                    POM_VERSION = readMavenPom().getVersion()
                    if (BRANCH_NAME.startsWith('release/')) {
                        RELEASE = true
                        echo "Branch '${BRANCH_NAME}' is a release branch."
                    } else if (BRANCH_NAME.startsWith('develop')) {
                        RELEASE = false
                         echo "Branch '${BRANCH_NAME}' is a develop branch."
                    }
                }
            }
        }
        stage('Build') {
            steps {
                sh "mvn clean install -DskipTests"
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/*.jar'
                }
            }
        }
    }
}
