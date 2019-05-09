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
        sh "mvn -f remocra/pom.xml -e -Duser.home=${env.WORKSPACE} -s /tmp/settings.xml -DnewVersion=\$(git describe) versions:set"
        sh "mvn -f remocra/pom.xml -e -U -Duser.home=${env.WORKSPACE} -s /tmp/settings.xml -Dmaven.compiler.fork=true clean verify -P modeinfo-able"
      }
      post {
        success {
          archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/**/*.war', fingerprint: true
        }
        unstable {
          archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/**/*.war', fingerprint: true
        }
        always {
          sh "mvn -f remocra/pom.xml -e -Duser.home=${env.WORKSPACE} -s /tmp/settings.xml versions:revert"
          sh "git checkout -- ."
        }
      }
    }
  }
}
