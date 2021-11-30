
pipeline {
  /*
    Description: Deployment pipeline
   */

  agent { label "jenkins-slave" }

  options {
    buildDiscarder(logRotator(daysToKeepStr: "7", numToKeepStr: "13"))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: "MINUTES")
  }

  environment {
    PROFILE = "dev"
  }

  stages {
    stage('Show Variables') {
      steps {
        script {
          sh 'make devops-print-variables'
        }
      }
    }

    stage("Deploy jMeter"){
      steps {
        script {
          sh 'make deploy-jmeter-namespace PROFILE=${env.PROFILE}'
          // Wait for jMeter pods to be available
          sh """build/jenkins/scripts/check_pods.sh jmeter-master uec-dos-api-sfsa-dev-jmeter 5 30"""
          sh """build/jenkins/scripts/check_pods.sh jmeter-slave uec-dos-api-sfsa-dev-jmeter 5 30"""
          }
        }
      }

    stage("Run Jmeter"){
      steps {
        script {
          sh 'make run-jmeter PROFILE=${env.PROFILE}'
        }
        // Make jMeter test report files available as build artifacts
        dir('test-results') {
          archiveArtifacts artifacts: '**'
        }
      }
    }

    stage("Destroy jMeter") {
      steps {
        script {
          sh 'make destroy-jmeter-namespace PROFILE=${env.PROFILE}'
        }
      }
    }
  }

  post {
    always { sh "make clean" }
    success { sh "make pipeline-on-success" }
    failure { sh "make pipeline-on-failure" }
  }

}
