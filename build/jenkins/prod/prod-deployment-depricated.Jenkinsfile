pipeline {
  /*
    Description: Deployment pipeline to deploy the Service Search module into the Prod environment
   */
  agent { label 'jenkins-slave' }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '13'))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: 'MINUTES')
  }
  environment {
    BUILD_DATE = sh(returnStdout: true, script: "date -u +'%Y-%m-%dT%H:%M:%S%z'").trim()
  //GIT_TAG = sh(returnStdout: true, script: "git get current ref ...").trim()
  //PROFILE = [get it from the GIT_TAG ...]
    PROFILE = 'pd'
  }
  parameters {
        string(
            description: 'Enter image tag to deploy, e.g. 202103111417-e362c87',
            name: 'IMAGE_TAG',
            defaultValue: ''
        )
  }
  triggers { pollSCM('* * * * *') }
  stages {
    stage('Show Configuration') {
      steps { sh 'make show-configuration' }
    }
    stage('Deploy') {
      parallel {
        stage('Deploy: App') {
          agent { label 'jenkins-slave' }
          steps {
            sh 'make backup-data'
            sh 'make provision-infractructure'
            sh 'make deploy-artefact'
            sh 'make apply-data-changes'
            sh "make run-smoke-test PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG}"
            sh 'make pipeline-send-notification'
          }
        }
      }
    }
  }
  post {
    success { sh 'make pipeline-on-success' }
    failure { sh 'make pipeline-on-failure' }
  }
}
