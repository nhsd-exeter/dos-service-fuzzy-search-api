
pipeline {
  /*
    Description: Deployment pipeline
   */

  agent any

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

    stage('Prepare') {
      steps {
        script {
          sh ("aws sts get-caller identity")
          sh 'make prepare'
        }
      }
    }

    stage("Deploy jMeter"){
      steps {
        script {
          sh "make deploy-jmeter-namespace PROFILE=${env.PROFILE}"
          }
        }
      }

    stage("Run Performance Tests"){
      steps {
        script {
          sh "make run-jmeter-performance-test PROFILE=${env.PROFILE}"
        }
        // Make jMeter test report files available as build artifacts
          archiveArtifacts artifacts: 'performance-test-results/**'
      }
    }

    stage("Run Load Tests"){
      steps {
        script {
          sh "make run-jmeter-load-test PROFILE=${env.PROFILE}"
        }
        // Make jMeter test report files available as build artifacts
          archiveArtifacts artifacts: 'load-test-results/**'
      }
    }

    stage("Run Stress Tests"){
      steps {
        script {
          sh "make run-jmeter-stress-test PROFILE=${env.PROFILE}"
        }
        // Make jMeter test report files available as build artifacts
          archiveArtifacts artifacts: 'stress-test-results/**'
      }
    }

    stage("Destroy jMeter") {
      steps {
        script {
          sh "make destroy-jmeter-namespace PROFILE=${env.PROFILE}"
        }
      }
    }
  }

  post {
    always { sh "make clean" }
    success { sh "make pipeline-on-success" }
    failure {
      sh "make destroy-jmeter-namespace PROFILE=${env.PROFILE}"
      sh "make pipeline-on-failure"
    }
  }

}
