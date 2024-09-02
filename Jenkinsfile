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
                    // Fetch all branches - Multibranch Pipeline automatically handles this
                    BRANCH_NAME = env.BRANCH_NAME
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
                        sh "mvn versions:set -DnewVersion=\$(echo ${POM_VERSION} | sed 's/-SNAPSHOT//')"
                    } else if (BRANCH_NAME.startsWith('feature/')) {
                        RELEASE = false
                        echo "Branch '${BRANCH_NAME}' is a feature branch."
                        // Increase version and add '-SNAPSHOT'
                        sh "mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.nextPatchVersion}-SNAPSHOT"
                    } else {
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
        stage("Publish to Nexus Repository Manager") {
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";

                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: 'localhost:8081',
                        groupId: 'QA',
                        version: "${env.BUILD_ID}",
                        repository: 'releasemanagement-snapshots',
                        credentialsId: 'NexusLogin',
                        artifacts: [
                            [artifactId: 'Git-ReleaseManagement',
                            classifier: '',
                            file: artifactPath,
                            type: 'jar']
    ]
 )
                    }
                }
            }
        }

    }

}
