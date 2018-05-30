package common
def antVersion = 'Ant 1.8.2'
def jdkVersion = 'JDK 1.7u25'
def recipientList = 'mparker@germaniainsurance.com,udokku@germaniainsurance.com'

def checkoutSVN(targetDir, repoURL) {
    checkout([$class: 'SubversionSCM',
        locations: [
            [credentialsId: '9af7c2c1-b40c-47da-a713-c9fbe496c4a3',
                depthOption: 'infinity',
                quietOperation: true,
                local: targetDir,
                remote: repoURL
            ]
        ]
    ])
}

def sendEmailNotification(emailSubject, emailBody) {
    def content = '${SCRIPT,template="promote-html.template"}'
    emailext(
        subject: emailSubject,
        to: 'mparker@germaniainsurance.com,udokku@germaniainsurance.com',
        mimeType: 'text/html',
        body: content
    )
}

node() {
  stage('Clean Workspace') {
      deleteDir()
  }
  stage("Code Checkout") {
      println("===== Checking Out ${env.PROJ_NAME} ======")
      checkoutSVN("${env.PROJ_NAME}/modules/configuration", "https://gsvnpapp1.germania-ins.com/svn/guidewire/InsuranceSuite/applications/branches/Release_${env.PROJ_VERSION}/${env.PROJ_NAME}/modules/configuration")
      println("===== Checking Out PolicyCenter ======")
      checkoutSVN("${env.PROJ_NAME}/modules/configuration", "https://gsvnpapp1.germania-ins.com/svn/guidewire/InsuranceSuite/applications/branches/Release_${env.PROJ_VERSION}/PolicyCenter/modules/configuration")
      println("===== Checking Out BillingCenter ======")
      checkoutSVN("${env.PROJ_NAME}/modules/configuration", "https://gsvnpapp1.germania-ins.com/svn/guidewire/InsuranceSuite/applications/branches/Release_${env.PROJ_VERSION}/BillingCenter/modules/configuration")
      println("===== Checking Out ClaimCenter ======")
      checkoutSVN("ext_temp/${env.PROJ_NAME}/modules/configuration", "https://gsvnpapp1.germania-ins.com/svn/guidewire/InsuranceSuite/applications/branches/Release_${env.PROJ_VERSION}/ClaimCenter/modules@HEAD")
  }
  stage("Build gw_update") {
      withAnt(installation: "${antVersion}", jdk: "${jdkVersion}") {
          withEnv(["ANT_HOME=${tool antVersion}"]) {
              sh "$ANT_HOME/bin/ant gw_update -f ${HUDSON_HOME}/buildfiles/ClaimCenter/continuous-build.xml -DforceUnzip=true -Dbase.product.zip=ClaimCenter805.zip -Dcustomer.workspace.home=${WORKSPACE}"
          }
      }
  }
  stage("Build gw_update") {
      withAnt(installation: "${antVersion}", jdk: "${jdkVersion}") {
          withEnv(["ANT_HOME=${tool antVersion}"]) {
              sh "$ANT_HOME/bin/ant gw_update -f ${HUDSON_HOME}/buildfiles/PolicyCenter/continuous-build.xml -DforceUnzip=true -Dbase.product.zip=PolicyCenter805.zip -Dcustomer.workspace.home=${WORKSPACE}"
          }
      }
  }
  stage("Build gw_update") {
      withAnt(installation: "${antVersion}", jdk: "${jdkVersion}") {
          withEnv(["ANT_HOME=${tool antVersion}"]) {
              sh "$ANT_HOME/bin/ant gw_update -f ${HUDSON_HOME}/buildfiles/BillingCenter/continuous-build.xml -DforceUnzip=true -Dbase.product.zip=BillingCenter805.zip -Dcustomer.workspace.home=${WORKSPACE}"
          }
      }
  }
  stage("port.override") {
      withAnt(installation: "${antVersion}", jdk: "${jdkVersion}") {
          withEnv(["ANT_HOME=${tool antVersion}"]) {
              sh "$ANT_HOME/bin/ant port.override -f ${HUDSON_HOME}/buildfiles/ClaimPortal/continuous-build.xml -Dport.url=8081 -Dcustomer.workspace.home=${WORKSPACE}"
          }
      }
  }
  stage("clean-dist-portal") {
      withAnt(installation: "${antVersion}", jdk: "${jdkVersion}") {
          withEnv(["ANT_HOME=${tool antVersion}"]) {
              sh "$ANT_HOME/bin/ant clean-dist-portal -f ${HUDSON_HOME}/buildfiles/ClaimPortal/continuous-build.xml -Dcustomer.workspace.home=${WORKSPACE}"
          }
      }
  }

  stage("setup BillingCenter") {
      sh '''cd ${WORKSPACE}/BillingCenter
      bin / gwbc.sh dev - stop
      bin / gwbc.sh dev - dropdb
      bin / gwbc.sh dev - start & ''
      '
  }

  stage("setup PolicyCenter") {
      sh ''
      'cd ${WORKSPACE}/PolicyCenter
      bin / gwpc.sh dev - stop
      bin / gwpc.sh dev - dropdb
      bin / gwpc.sh dev - start & '''
  }

  stage("setup ClaimCenter") {
      sh '''cd ${WORKSPACE}/ClaimCenter
      bin / gwcc.sh dev - stop
      bin / gwcc.sh dev - dropdb
      bin / gwcc.sh dev - start & '''
  }

  stage("setup ClaimCenter") {
      sleep time: 90, unit: 'SECONDS'
      sh '''cd ${WORKSPACE}/EdgeAccountManagementPortal
      export NVM_DIR = "/home/jenkins/.nvm" [-s "$NVM_DIR/nvm.sh"] && .
      "$NVM_DIR/nvm.sh"
      nvm use 5.6.0
      npm install
      npm run grunt '''
  }

  stage("portal.build.properties") {
      withAnt(installation: "${antVersion}", jdk: "${jdkVersion}") {
          withEnv(["ANT_HOME=${tool antVersion}"]) {
              sh "$ANT_HOME/bin/ant portal.build.properties -f ${HUDSON_HOME}/buildfiles/ClaimPortal/continuous-build.xml -Dcustomer.workspace.home=${WORKSPACE} -Dportal.dist.dir=${WORKSPACE}/EdgeAccountManagementPortal/dist"
          }
      }
  }
  stage("Archive Artifacts") {
      archiveArtifacts allowEmptyArchive: true, artifacts: "EdgeAccountManagementPortal/dist/**/*.*", onlyIfSuccessful: true
  }
  stage('Send Email') {
      sendEmailNotification("Build ${env.JOB_NAME} - ${env.BUILD_STATUS} - [${env.BUILD_NUMBER}]", "STARTED: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]: Check console output at - ${env.BUILD_URL}")
  }
}