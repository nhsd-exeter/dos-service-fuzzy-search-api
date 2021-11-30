def jmeterNamespace
def jMeterNamespaceExists
pipeline {
  /*
    Description: Deployment pipeline
   */

  agent { label "jenkins-slave" }

  options {
    buildDiscarder(logRotator(daysToKeepStr: "7", numToKeepStr: "13"))
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
    timeout(time: 30, unit: "MINUTES")
  }

  environment {
    PROFILE = "dev"
  }

  stages {
    stage('Show Variables') {
      steps {
        script {
          sh 'make devops-print-variables'
        }
      }
    }

    stage("Deploy jMeter"){
      steps {
        script {
          sh "make deploy-jmeter-namespace PROFILE=${env.PROJECT_BUILD_TAG}"
          // Wait for jMeter pods to be available
          sh """build/jenkins/scripts/check_pods.sh jmeter-master uec-dos-api-sfsa-dev-jmeter 5 30"""
          sh """build/jenkins/scripts/check_pods.sh jmeter-slave uec-dos-api-sfsa-dev-jmeter 5 30"""
          }
        }
      }

    stage("Run Jmeter"){
        {
          sh """kubectl config set-context --current --namespace=${jmeterNamespace}"""
          sh """${jmeterScriptsDir}/jmeter_stop.sh"""
          sh """${jmeterScriptsDir}/start_test.sh ${jmeterTestDir} ${jmeterTestDir}/${jmxFile}"""
        }
      }
      // Make jMeter test report files available as build artifacts
      dir('test-results') {
        archiveArtifacts artifacts: '**'
      }
    }

    stage("Destroy jMeter") {
      script {
        sh "make destroy-jmeter-namespace PROFILE=${env.PROJECT_BUILD_TAG}"
      }
    }
  }

  post {
    always { sh "make clean" }
    success { sh "make pipeline-on-success" }
    failure { sh "make pipeline-on-failure" }
  }

}
