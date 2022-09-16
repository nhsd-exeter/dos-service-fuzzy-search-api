
pipeline {
  /*
    Description: Deployment pipeline
   */
  agent any

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 120, unit: "MINUTES")
  }

  environment {
    PROFILE = 'stg'
  }

  parameters {
        string(
            description: 'Enter image tag to deploy, e.g. 202103111417-e362c87',
            name: 'IMAGE_TAG',
            defaultValue: ''
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
    stage('Check Py Lib Folder') {
      steps {
        script {
          sh 'make create-lambda-deploy-dir'
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
    stage('Plan Base Infrastructure') {
      steps {
        script {
          sh "make plan-base PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Plan ETL Infrastructure') {
      steps {
        script {
          sh "make plan-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Destory Env') {
      steps {
        script {
          sh "make destroy PROFILE=${env.PROFILE}"
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
