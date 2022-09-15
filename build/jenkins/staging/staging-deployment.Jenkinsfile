
pipeline {
  /*
    Description: Deployment pipeline
   */
  agent any

  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 45, unit: "MINUTES")
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
    stage('Destory Env') {
      steps {
        script {
          sh 'make destroy PROFILE=${env.PROFILE}'
        }
      }
    }
  }
  post {
    success { sh 'make pipeline-on-success' }
    failure { sh 'make pipeline-on-failure' }
  }
}
