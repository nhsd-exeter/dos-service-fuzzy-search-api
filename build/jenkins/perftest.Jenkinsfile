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
          sh "make prepare"
        }
      }
    }
    // stage("Plan Infrastructure") {
    //   steps {
    //     script {
    //       sh "make plan PROFILE=${env.PROFILE}"
    //     }
    //   }
    // }
    // stage("Populate Cognito Pool"){
    //   steps {
    //     script {
    //       sh "make project-populate-cognito PROFILE=${env.PROFILE}"
    //     }
    //   }
    // }
    // stage("Provision Infrastructure") {
    //   steps {
    //     script {
    //       sh "make provision PROFILE=${env.PROFILE}"
    //     }
    //   }
    // }
    // stage("Populate Search DB") {
    //   steps {
    //     script {
    //       echo "to do"
    //     }
    //   }
    // }
    // stage("Deploy API") {
    //   steps {
    //     script {
    //       sh "make deploy PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG}"
    //     }
    //   }
    // }
    // stage("Monitor Deployment") {
    //   steps {
    //     script {
    //       echo "to do"
    //     }
    //   }
    // }
    // stage("Smoke Tests") {
    //   steps {
    //     script {
    //       sh "make run-smoke-test PROFILE=${env.PROFILE} API_IMAGE_TAG=${IMAGE_TAG}"
    //     }
    //   }
    // }

    stage("Deploy jMeter"){
      steps {
        script {
          // Prevent concurrent jMeter deployments
          lock ("${jmeterNamespace}"){
            // withEnv([ "AWS_ACCESS_KEY_ID=${awsAccessKeyId}",
            //   "AWS_SECRET_ACCESS_KEY=${awsSecretAccessKey}",
            //   "AWS_SESSION_TOKEN=${awsSessionToken}",
            //   "KUBECONFIG=${kubeconfig}"
            //   ])
            // {
              def jmeterNamespace = "${env.PROJECT_GROUP_SHORT}-${env.PROJECT_NAME_SHORT}-${env.PROFILE}-jmeter"
              jMeterNamespaceExists = sh( script: """ kubectl get ns | awk '{print \$1}' | grep '^${jmeterNamespace}\$' || true """, returnStdout: true).trim()
              if ( jMeterNamespaceExists != "${jmeterNamespace}" ) {
                echo "namespace ${jmeterNamespace} doesn't exist, creating it..."
                sh (""" kubectl create ns ${jmeterNamespace} """)
              }
              else {
                echo "namespace ${jmeterNamespace} already exists"
              }
              // dir ( jMeterK8sDir ){
              //   sh """kubectl apply -n ${jmeterNamespace} -f jmeter_slaves_deploy.yaml"""
              //   sh """kubectl apply -n ${jmeterNamespace} -f jmeter_slaves_svc.yaml"""
              //   sh """kubectl apply -n ${jmeterNamespace} -f jmeter_master_deploy.yaml"""
              // }
              // // Wait for jMeter pods to be available
              // sh """${jenkinsScriptsDir}/check_pods.sh jmeter-master ${jmeterNamespace} 5 30"""
              // sh """${jenkinsScriptsDir}/check_pods.sh jmeter-slave ${jmeterNamespace} 5 30"""
            // }
          }
        }
      }
    }

    // stage("Run Jmeter"){
    //   steps {
    //     script {
    //       // Prevent concurrent jMeter executions in the same namespace
    //       lock ("${jmeterNamespace}"){
    //         // withEnv([ "AWS_ACCESS_KEY_ID=${awsAccessKeyId}",
    //         //   "AWS_SECRET_ACCESS_KEY=${awsSecretAccessKey}",
    //         //   "AWS_SESSION_TOKEN=${awsSessionToken}",
    //         //   "KUBECONFIG=${kubeconfig}"
    //         //   ])
    //         // {
    //           sh ("""sed -i 's|REPLACE_WITH_FQDN|${fqdn}|g' ${jmeterTestDir}/${jmxFile}""")
    //           sh ("""sed -i 's|REPLACE_WITH_PATH|${path}|g' ${jmeterTestDir}/${jmxFile}""")
    //           sh """kubectl config set-context --current --namespace=${jmeterNamespace}"""
    //           sh """${jmeterScriptsDir}/jmeter_stop.sh"""
    //           sh """${jmeterScriptsDir}/start_test.sh ${jmeterTestDir} ${jmeterTestDir}/${jmxFile}"""
    //         // }
    //       }
    //       // Make jMeter test report files available as build artifacts
    //       dir('test-results') {
    //         archiveArtifacts artifacts: '**'
    //       }
    //     }
    //   }
    // }

    // stage("Destroy jMeter") {
    //   // Prevent concurrent jMeter destroys
    //   lock ("${jmeterNamespace}"){
    //     withEnv([ "AWS_ACCESS_KEY_ID=${awsAccessKeyId}",
    //       "AWS_SECRET_ACCESS_KEY=${awsSecretAccessKey}",
    //       "AWS_SESSION_TOKEN=${awsSessionToken}",
    //       "KUBECONFIG=${kubeconfig}"
    //       ])
    //     {
    //       sh """kubectl delete ns ${jmeterNamespace}"""
    //     }
    //   }
    // }



  }

  post {
    always { sh "make clean" }
    success { sh "make pipeline-on-success" }
    failure { sh "make pipeline-on-failure" }
  }

}
