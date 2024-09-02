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
                    // Using GIT_BRANCH to determine the branch name
                    BRANCH_NAME = env.GIT_BRANCH?.replaceAll('origin/', '') ?: sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    echo "Building branch: ${BRANCH_NAME}"
                }
            }
        }
        stage('Determine Version Strategy') {
            when {
                expression {
                    env.BRANCH_NAME.startsWith('release/') || env.BRANCH_NAME.startsWith('develop')
                }
            }
            steps {
                script {
                    POM_VERSION = readMavenPom().getVersion()
                    if (BRANCH_NAME.startsWith('release/')) {
                        RELEASE = true
                        echo "Branch '${BRANCH_NAME}' is a release branch."
                        // Remove '-SNAPSHOT' from version
//                         sh "mvn versions:set -DnewVersion=\$(echo ${POM_VERSION} | sed 's/-SNAPSHOT//')"
                    } else if (BRANCH_NAME.startsWith('develop')) {
                        RELEASE = false
                         echo "Branch '${BRANCH_NAME}' is a develop branch."
                    }
//                     else if (BRANCH_NAME.startsWith('feature/')) {
//                         RELEASE = false
//                         echo "Branch '${BRANCH_NAME}' is a feature branch."
//                     } else if (BRANCH_NAME.startsWith('hotfix/')) {
//                         RELEASE = false
//                          echo "Branch '${BRANCH_NAME}' is a hotfix branch."
//                     }else if (BRANCH_NAME.startsWith('master')) {
//                         RELEASE = false
//                          echo "Branch '${BRANCH_NAME}' is a master branch."
//                     } else {
//                         error "Branch '${BRANCH_NAME}' does not match 'release/' or 'feature/' prefix."
//                     }
                }
            }
        }
        stage('Prepare Version') {
            when {
                expression {
                    env.BRANCH_NAME.startsWith('release/') || env.BRANCH_NAME.startsWith('develop')
                }
            }
            steps {
                script {
                    def version = POM_VERSION
                    def newVersion

                    if (IS_RELEASE) {
                        // Handle release version: Remove '-SNAPSHOT' and increment the major version
                        newVersion = version.replaceAll("-SNAPSHOT", "")
                        def versionParts = newVersion.split('\\.')
                        newVersion = "${versionParts[0].toInteger() + 1}.0.0"
                        echo "Releasing version: ${newVersion}"
                    } else {
                        // Handle snapshot version: Increment the minor version and add '-SNAPSHOT'
                        def versionParts = version.replaceAll("-SNAPSHOT", "").split('\\.')
                        newVersion = "${versionParts[0]}.${versionParts[1].toInteger() + 1}.0-SNAPSHOT"
                        echo "Preparing snapshot version: ${newVersion}"
                    }
                    // Update the POM version
//                     sh "mvn versions:set -DnewVersion=${newVersion}"
//
//                     // Commit the version change
//                     sh """
//                         git config user.name "Jenkins"
//                         git config user.email "jenkins@mycompany.com"
//                         git add pom.xml
//                         git commit -m "Setting version to ${newVersion}"
//                     """
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
