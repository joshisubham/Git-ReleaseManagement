pipeline {
    agent any

    tools {
        jdk "openjdk-17"
        maven "Maven3"
    }

    stages { 
        stage('Fetch code') {
          steps{
              git branch: 'master', url:'https://github.com/joshisubham/Git-ReleaseManagement.git'
          }  
        }
        stage('Build') {
            steps {
                sh "mvn clean install -DskipTests"
            }
        }
        stage('Test'){
            steps {
                sh 'mvn test'
            }
            post {
                success {
                    // archiveArtifacts 'target/*.jar'
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
