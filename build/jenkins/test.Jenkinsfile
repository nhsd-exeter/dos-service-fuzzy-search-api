pipeline {
  /*
    Description: Development pipeline to build test push and deploy to nonprod
   */
  agent { label "jenkins-slave" }

  environment {
    PROFILE = "test"
  }

  options {
    buildDiscarder(logRotator(daysToKeepStr: "7", numToKeepStr: "13"))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: "MINUTES")
  }

  triggers { pollSCM("* * * * *") }

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
          sh "make prepare"
        }
      }
    }
    /*
    stage("Plan Infrastructure") {
      steps {
        script {
          sh "make plan PROFILE=${env.PROFILE}"
        }
      }
    }
    */
    stage('Derive Build Tag') {
      steps {
        script {
          env.PROJECT_BUILD_TAG = sh(returnStdout: true, script: 'make derive-build-tag').trim()
        }
      }
    }
    stage('Build API') {
      steps {
        script {
          sh "make build"
        }
      }
    }
    /*
    stage('Unit Test') {
      steps {
        script {
          sh "make test"
        }
      }
    }
    */
    stage('Run Contract Tests') {
      steps {
        script {
          sh "make start"
          sh "newman run test/contract/FuzzySearchApiContractTests_collection.json -e test/contract/environment.json --insecure"
          // echo "to do"
        }
      }
    }
    stage('Final') {
      steps {
        script {
          sh "make stop"
        }
      }
    }
  }
  post {
    success { sh "make pipeline-on-success" }
    failure { sh "make pipeline-on-failure" }
  }

}
