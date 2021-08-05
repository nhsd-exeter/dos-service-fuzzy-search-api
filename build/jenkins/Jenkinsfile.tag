pipeline {
  /*
    Tags the release image
   */

  parameters {
        string(
            description: 'Enter image tag for release candidate, e.g. 202103111417-e362c87',
            name: 'IMAGE_TAG',
            defaultValue: ''
        )
    string(
            description: 'Enter release tag, e.g. release-20210401',
            name: 'RELEASE_TAG',
            defaultValue: ''
        )
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '5'))
    disableConcurrentBuilds()
  }

  stages {
    stage("Show Variables") {
      steps {
        script {
          sh 'make devops-print-variables'
        }
      }
    }
    stage("Prepare") {
      steps {
        script {
          sh 'make project-prepare'
        }
      }
    }
    stage("Create Release Image") {
      steps {
        script {
          sh "make project-tag-release TAG=${IMAGE_TAG} NEW_TAG=${RELEASE_TAG}"
        }
      }
    }
  }
}
