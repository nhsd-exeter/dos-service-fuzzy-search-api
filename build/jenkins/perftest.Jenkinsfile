
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
    stage('Prepare') {
      steps {
        script {
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
        dir('test-results') {
          archiveArtifacts artifacts: '**'
        }
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
    failure { sh "make pipeline-on-failure" }
  }

}
