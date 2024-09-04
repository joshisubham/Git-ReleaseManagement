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
        MAVEN_SETTINGS = credentials('maven-settings-xml')
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
                    def releaseType = input(
                        id: 'ReleaseType', message: 'Select Release Type',
                        parameters: [
                            choice(choices: ['major', 'minor'], description: 'Choose the type of release:', name: 'ReleaseType')
                        ]
                    )
                    def version = POM_VERSION.replaceAll("-SNAPSHOT", "")
                    def versionParts = version.split('\\.')

                    def newVersion
                    if (releaseType == 'major') {
                        newVersion = "${versionParts[0].toInteger() + 1}.0.0"
                    } else if (releaseType == 'minor') {
                        newVersion = "${versionParts[0]}.${versionParts[1].toInteger() + 1}.0"
                    }
                    echo "Releasing version: ${newVersion}"

                    // Update the POM version and perform release
                    sh "mvn versions:set -DnewVersion=${newVersion} --settings ${MAVEN_SETTINGS}"

                    sshagent(credentials: ['GitCreds']) {
                        sh """
                            git config user.name "Jenkins"
                            git config user.email "subhamjoshi466@gmail.com"
                            git reset --hard
                            # Ensure we're on the correct branch and clean state
                            git checkout ${env.GIT_BRANCH}
                            git reset --hard HEAD

                            # Pull the latest changes from the remote branch
                            git pull --rebase origin ${env.GIT_BRANCH}

                            # Set remote URL
                            git remote set-url origin ${SSH_GIT_URL}

                            # Make changes and push
                            git add pom.xml
                            git clean -f -d  # Remove untracked files and directories
                            git commit -m "Setting version to ${newVersion}" --author="Jenkins <subhamjoshi466@gmail.com>" || true
                            git push origin ${env.GIT_BRANCH} || true
                            
                            # Perform Maven release
                            mvn --batch-mode clean release:prepare release:perform -Dresume=false -DautoVersionSubmodules=true -DdryRun=false -Darguments="-DskipITs -DskipTests" -Dmaven.test.skip=true -Dtag=spring-jenkins-${newVersion} -DreleaseVersion=${newVersion} -DdevelopmentVersion=${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}-SNAPSHOT --settings ${MAVEN_SETTINGS}
                        """
                    }

                }
            }
        }
        stage('Prepare Hotfix') {
            when {
                expression {
                    BRANCH_NAME.startsWith('hotfix')
                }
            }
            steps {
                script {
                    def version = POM_VERSION.replaceAll("-SNAPSHOT", "")
                    def versionParts = version.split('\\.')
                    def newVersion = "${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}"

                    def hotfixType = input(
                        id: 'HotfixType', message: 'Select Hotfix Type',
                        parameters: [
                            choice(choices: ['hotfix', 'skip'], description: 'Choose the type of release (select "skip" to bypass):', name: 'HotfixType')
                        ]
                    )
                    if (hotfixType == 'hotfix') {
                        echo "Releasing hotfix version: ${newVersion}"
                        sh "mvn versions:set -DnewVersion=${newVersion}"
                        sshagent(credentials: ['GitCreds']) {
                            sh """
                                git config user.name "Jenkins"
                                git config user.email "subhamjoshi466@gmail.com"
                                git reset --hard
                                # Ensure we're on the correct branch and clean state
                                git checkout ${env.GIT_BRANCH}
                                git reset --hard HEAD

                                # Set remote URL
                                git remote set-url origin ${SSH_GIT_URL}

                                # Make changes and push
                                git add pom.xml
                                git clean -f -d  # Remove untracked files and directories
                                git commit -m "Setting version to ${newVersion}" --author="Jenkins <subhamjoshi466@gmail.com>" || true
                                git push origin ${env.GIT_BRANCH} || true

                                # Perform Maven release
                                mvn --batch-mode clean release:prepare release:perform -Dresume=false -DautoVersionSubmodules=true -DdryRun=false -Darguments="-DskipITs -DskipTests" -Dmaven.test.skip=true -Dtag=spring-jenkins-${newVersion} -DreleaseVersion=${newVersion} -DdevelopmentVersion=${versionParts[0]}.${versionParts[1]}.${versionParts[2].toInteger() + 1}-SNAPSHOT
                            """
                        }
//                         // Commit the version change
//                         sshagent(credentials: ['GitCreds']) {
//                             sh """
//                                 git config user.name "Jenkins"
//                                 git config user.email "subhamjoshi466@gmail.com"
//                                 # Ensure we're on the correct branch and clean state
//                                 git checkout ${env.GIT_BRANCH}
//                                 git reset --hard HEAD
//
//                                 # Set remote URL
//                                 git remote set-url origin ${SSH_GIT_URL}
//                                 git add pom.xml
//                                 git commit -m "Setting hotfix version to ${newVersion}"
//                                 git push origin ${BRANCH_NAME}
//                             """
//                         }
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
