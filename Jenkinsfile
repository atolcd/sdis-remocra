#!/usr/bin/env groovy

pipeline {
  agent {
    label 'docker&&ovh'
  }
  stages {
    stage('Build') {
      agent {
        docker {
          image 'cvagner/docker-jdk-maven-sencha-cmd:7-3.6.0-3.0.2'
          args '-v /var/lib/jenkins/.m2/settings.xml:/tmp/settings.xml:ro'
          reuseNode true
        }
      }
      steps {
        // Run the maven build (mvn is in the PATH in the Docker image)
        sh "mvn -e -Duser.home=${env.WORKSPACE} -s /tmp/settings.xml -U clean"
        sh "mvn -e -Duser.home=${env.WORKSPACE} -s /tmp/settings.xml -U verify -P modeinfo-able -P update"
        sh "mvn -e -Duser.home=${env.WORKSPACE} -s /tmp/settings.xml -U verify -P modeinfo-able -P install"
      }
      post {
        success {
          archiveArtifacts allowEmptyArchive: true, artifacts: 'remocra/target/*.war', fingerprint: true
          archiveArtifacts allowEmptyArchive: true, artifacts: 'dist/target/*.zip', fingerprint: true
        }
        unstable {
          archiveArtifacts allowEmptyArchive: true, artifacts: 'remocra/target/*.war', fingerprint: true
          archiveArtifacts allowEmptyArchive: true, artifacts: 'dist/target/*.zip', fingerprint: true
        }
        always {
          sh "git checkout -- ."
        }
      }
    }
  }
}
