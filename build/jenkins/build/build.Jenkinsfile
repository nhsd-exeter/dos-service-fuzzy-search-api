pipeline {
  /*
    Description: Development pipeline to build test push and deploy to the development environment.
   */
  agent {
    label 'jenkins-slave'
  }

  environment {
    PROFILE = 'dev'
  }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }

  triggers { pollSCM('* * * * *') }

  stages {
    stage('Prepare') {
      steps {
        script {
          sh 'make prepare'
        }
      }
    }
    stage('Show Variables') {
      steps {
        script {
          sh 'make devops-print-variables'
        }
      }
    }
    stage('Derive Build Tag') {
      steps {
        script {
          env.PROJECT_BUILD_TAG = sh(returnStdout: true, script: 'make derive-build-tag').trim()
        }
      }
    }
    stage('Scan Dependencies') {
      steps {
        script {
          //sh 'make scan'
          echo 'Skipping dependency scan due to issue in springfox-swagger library'
        }
        //archiveArtifacts artifacts: 'reports/**'
      }
    }
    stage('Build API') {
      steps {
        script {
          sh "make build VERSION=${env.PROJECT_BUILD_TAG}"
        }
      }
    }
    stage('Unit Test') {
      steps {
        script {
          sh 'make unit-test'
        }
      }
    }
    stage('Run Contract Tests') {
      steps {
        script {
          sh "make run-contract-tests VERSION=${env.PROJECT_BUILD_TAG}"
        }
      }
    }
    stage('Push API Image to ECR') {
      steps {
        script {
          sh "make push VERSION=${env.PROJECT_BUILD_TAG}"
        }
      }
    }
    stage('Image Build Tag') {
      steps {
        script {
          sh "echo 'Image Build Tag: '${env.PROJECT_BUILD_TAG}"
        }
      }
    }
  }

  post {
    always  {
      sh 'make clean'
      publishHTML([allowMissing: false,
            alwaysLinkToLastBuild: false,
            keepAll: true,
            reportDir: 'test/contract/reports/',
            reportFiles: 'report.html',
            reportName: 'Fuzzy Test Report',
            reportTitles: 'Fuzzy Test Report'
        ])
      }
    success { sh 'make pipeline-on-success' }
    failure { sh 'make pipeline-on-failure' }
  }
}
