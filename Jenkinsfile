pipeline {
    agent any

    tools {
        jdk "openjdk-17"
        maven "Maven3"
    }

    environment {
        POM_VERSION = ""
        BRANCH_NAME = ""
        RELEASE = false
    }

    stages {
        stage('Fetch code') {
            steps {
                script {
                    // Using GIT_BRANCH to determine the branch name
                    BRANCH_NAME = env.GIT_BRANCH?.replaceAll('origin/', '') ?: sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    echo "Building branch: ${BRANCH_NAME}"
                }
            }
        }
        stage('Determine Version Strategy') {
            steps {
                script {
                    POM_VERSION = readMavenPom().getVersion()
                    if (BRANCH_NAME.startsWith('release/')) {
                        RELEASE = true
                        echo "Branch '${BRANCH_NAME}' is a release branch."
                        // Remove '-SNAPSHOT' from version
//                         sh "mvn versions:set -DnewVersion=\$(echo ${POM_VERSION} | sed 's/-SNAPSHOT//')"
                    } else if (BRANCH_NAME.startsWith('feature/')) {
                        RELEASE = false
                        echo "Branch '${BRANCH_NAME}' is a feature branch."
                    }else if (BRANCH_NAME.startsWith('hotfix/')) {
                        RELEASE = false
                         echo "Branch '${BRANCH_NAME}' is a hotfix branch."
                    }else if (BRANCH_NAME.startsWith('master')) {
                        RELEASE = false
                         echo "Branch '${BRANCH_NAME}' is a master branch."
                    }else if (BRANCH_NAME.startsWith('develop')) {
                        RELEASE = false
                         echo "Branch '${BRANCH_NAME}' is a develop branch."
                    }else {
                        error "Branch '${BRANCH_NAME}' does not match 'release/' or 'feature/' prefix."
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
