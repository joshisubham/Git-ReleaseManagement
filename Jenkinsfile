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
        SSH_GIT_URL = "https://github.com/joshisubham/Git-ReleaseManagement.git"
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
        stage('Prepare Release Version') {
            when {
                expression {
                    BRANCH_NAME.startsWith('release')
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
                    echo "Release branch version: ${BRANCH_NAME} :  releaseType: ${releaseType}"
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
                    }
                    // Update the POM version and perform release
                    sh "mvn versions:set -DnewVersion=${newVersion}"

                    // Push the changes
                    sh """
                        git config user.name "Jenkins"
                        git config user.email "subhamjoshi466@gmail.com"
                        git add pom.xml
                        git commit -m "Setting version to ${newVersion}"
                        git push origin ${BRANCH_NAME}
                    """

                    // Perform the Maven release
                    sh """
                        git remote set-url origin ${SSH_GIT_URL}
                        mvn --batch-mode clean release:prepare release:perform -Dresume=false -DautoVersionSubmodules=true -DdryRun=false -Darguments="-DskipITs -DskipTests" -Dmaven.test.skip=true -Dtag=maps-imageService-${newVersion} -DreleaseVersion=${newVersion} -DdevelopmentVersion=${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}-SNAPSHOT
                    """

                }
            }
        }
        stage('Prepare Hotfix') {
            when {
                expression {
                    BRANCH_NAME.startsWith('release')
                }
            }
            steps {
                script {
                    def version = POM_VERSION
                    def newVersion
                    def releaseType = input(
                        id: 'ReleaseType', message: 'Select Hotfix Type',
                        parameters: [
                            choice(choices: ['hotfix', 'skip'], description: 'Choose the type of release (select "skip" to bypass):', name: 'ReleaseType')
                        ]
                    )

                    if (releaseType == 'hotfix') {
                        // Handle hotfix: Remove '-SNAPSHOT', increment the patch version
                        newVersion = version.replaceAll("-SNAPSHOT", "")
                        def versionParts = newVersion.split('\\.')
                        newVersion = "${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}"
                        echo "Releasing hotfix version: ${newVersion}"

                        // Update the POM version
                        sh "mvn versions:set -DnewVersion=${newVersion}"

                        // Commit the version change
                        sh """
                            git config user.name "Jenkins"
                            git config user.email "subhamjoshi466@gmail.com"
                            git add pom.xml
                            git commit -m "Setting version to ${newVersion}"
                        """
                    } else {
                        echo "Hotfix step skipped."
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
