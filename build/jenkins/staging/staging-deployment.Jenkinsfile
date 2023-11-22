
pipeline {
  /*
    Description: Deployment pipeline
   */
  agent {
    label 'jenkins-slave'
  }

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
    stage('Check IMAGE_TAG parameter') {
      steps {
        script {
          def pattern = /^[0-9]{12}-[a-f0-9]{7}$/

          if (!params.IMAGE_TAG.matches(pattern)) {
            error "Provided IMAGE_TAG '${params.IMAGE_TAG}' does not match the expected pattern. Aborting build."
          }
        }
      }
    }
    stage('Prepare for jenkins-slave run') {
      steps {
        script {
          sh "make pipeline-slave-prepare"
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
    failure {
      sh 'make terraform-remove-state-lock'
      sh 'make pipeline-on-failure'
    }
  }
}
