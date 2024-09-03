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
        stage('Prepare Version') {
            when {
                expression {
                    BRANCH_NAME.startsWith('develop') || BRANCH_NAME.startsWith('hotfix')
                }
            }
            steps {
                script {
                    def version = POM_VERSION
                    def newVersion
                    // Prompt user for release type: major or minor
                    def releaseType = input(
                        id: 'ReleaseType', message: 'Select Release Type',
                        parameters: [
                            choice(choices: ['major', 'minor'], description: 'Choose the type of release:', name: 'ReleaseType')
                        ]
                    )
                    if (releaseType == 'major') {
                        // Handle major release: Remove '-SNAPSHOT', increment the major version
                        newVersion = version.replaceAll("-SNAPSHOT", "")
                        def versionParts = newVersion.split('\\.')
                        newVersion = "${versionParts[0].toInteger() + 1}.0.0"
                        echo "Releasing major version: ${newVersion}"
                    } else if (releaseType == 'minor') {
                        // Handle minor release: Remove '-SNAPSHOT', increment the minor version
                        newVersion = version.replaceAll("-SNAPSHOT", "")
                        def versionParts = newVersion.split('\\.')
                        newVersion = "${versionParts[0]}.${versionParts[1].toInteger() + 1}.0"
                        echo "Releasing minor version: ${newVersion}"
                    } else if (releaseType == 'hotfix') {
                        // Handle minor release: Remove '-SNAPSHOT', increment the minor version
                        newVersion = version.replaceAll("-SNAPSHOT", "")
                        def versionParts = newVersion.split('\\.')
                        newVersion = "${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}"
                        echo "Releasing hotfix version: ${newVersion}"
                    } else {
                        // Handle snapshot version: Increment the patch version and add '-SNAPSHOT'
                        def versionParts = version.replaceAll("-SNAPSHOT", "").split('\\.')
                        newVersion = "${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}-SNAPSHOT"
                        echo "Preparing snapshot version: ${newVersion}"
                    }

                    // Update the POM version
//                     sh "mvn versions:set -DnewVersion=${newVersion}"
//
//                     // Commit the version change
//                     sh """
//                         git config user.name "Jenkins"
//                         git config user.email "subhamjoshi466@gmail.com"
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
