pipeline {
  /*
    Description: Deployment pipeline to deploy the Service Search module into the Development environment.
   */

  agent { label 'jenkins-slave' }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }

  environment {
    PROFILE = 'dev'
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
    stage('Plan Base Infrastructure') {
      steps {
        script {
          sh "make plan-base PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Provision Base Infrastructure') {
      steps {
        script {
          sh "make provision-base PROFILE=${env.PROFILE}"
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
    stage('Provision ETL Infrastructure') {
      steps {
        script {
          sh "make provision-etl PROFILE=${env.PROFILE}"
        }
      }
    }
    stage('Deploy API') {
      steps {
        script {
          sh "make deploy PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG} MOCK_POSTCODE_IMAGE_TAG=${IMAGE_TAG}"
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
    stage('Run Service ETL') {
      steps {
        script {
          sh 'make apply-data-changes'
        }
      }
    }
    // stage('Smoke Tests') {
    //   steps {
    //     script {
    //       sh "make run-smoke-test PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG}"
    //     }
    //   }
    // }
  }
  post {
    always { sh 'make clean' }
    success { sh 'make pipeline-on-success' }
    failure { sh 'make pipeline-on-failure' }
  }
}
