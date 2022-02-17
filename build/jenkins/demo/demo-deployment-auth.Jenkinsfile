pipeline {
  /*
    Description: Deployment pipeline to deploy the infrastructure of the API Authentication module
    into the Demo environment.
   */

  agent { label 'jenkins-slave' }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }

  environment {
    PROFILE = 'demo'
  }

  parameters {
        string(
            description: 'Add default users to the user pool?',
            name: 'ADD_DEFAULT_USERS',
            defaultValue: 'false'
        )
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
          sh 'make prepare'
        }
      }
    }
    stage('Plan Infrastructure') {
      steps {
        script {
          sh "make plan_auth PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Provision Infrastructure') {
      steps {
        script {
          sh "make provision_auth PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Populate Cognito Pool') {
      steps {
        script {
          sh "make project-populate-cognito PROFILE=${env.PROFILE} ADD_DEFAULT_COGNITO_USERS=${ADD_DEFAULT_USERS}"
        }
      }
    }
  }

  post {
    always { sh 'make clean' }
    success { sh 'make pipeline-on-success' }
    failure { sh 'make pipeline-on-failure' }
  }
}
