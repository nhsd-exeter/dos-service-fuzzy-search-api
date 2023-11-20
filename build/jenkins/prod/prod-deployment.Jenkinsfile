pipeline {
  /*
    Description: Deployment pipeline to deploy the Service Search module into the Demo environment
   */

  agent {
    label 'jenkins-slave'
  }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }

  environment {
    PROFILE = 'pd'
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
    stage('Populate Project variables') {
      steps {
        script {
          sh 'make project-populate-application-variables'
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
    stage('Import Infrastructure') {
      steps {
        script {
          sh "make terraform-import-stack PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Plan Infrastructure') {
      steps {
        script {
          sh "make plan PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Provision Infrastructure') {
      steps {
        script {
          sh "make provision PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Deploy API') {
      steps {
        script {
          sh "make deploy PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG}"
        }
      }
    }
    stage('Monitor Deployment') {
      steps {
        script {
          sh 'make k8s-check-deployment-of-replica-sets'
        }
      }
    }
    stage('Monitor Route53 Connection') {
      steps {
        script {
          sh 'make monitor-r53-connection'
        }
      }
    }
    stage('Populate Cognito Store') {
      steps {
        script {
          sh 'make project-populate-cognito'
        }
      }
    }
    stage('Smoke Tests') {
      steps {
        script {
          sh "make run-smoke-test PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG}"
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
