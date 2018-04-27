def pipelinScript = "${WORKSPACE}/jobs/portal_build_pipeline"

def disableProj = [
    "GatewayPortalAgents": true,
    "AccountManagementPortal": true,
    "ClaimPortal": true

]

def list_projName = [
    "GatewayPortalAgents",
    "AccountManagementPortal",
    "ClaimPortal"
]

def list_Version = [
    "2.7.1"
]

list_projName.eachWithIndex { projName, idx ->
    list_Version.eachWithIndex { listVersion, idx1 ->

        def jobName = "Test_${projName}BuildR${listVersion}"
        def desc = "Job ${projName} is building with ${listVersion} version"

        pipelineJob(jobName) {

            description(desc)
            logRotator(30)
            disabled(disableProj[projName])

            throttleConcurrentBuilds {
                maxPerNode(1)
                maxTotal(2)
                categories(["${projName}Build"])
            }

            environmentVariables(PROJ_NAME: "${projName}", PROJ_VERSION: "${listVersion}")

            triggers {
                scm('H * * * *')
            }

            definition {
                cps {
                    script(readFileFromWorkspace(pipelinScript).stripIndent())
                    sandbox()
                }
            }
        }
    }
}