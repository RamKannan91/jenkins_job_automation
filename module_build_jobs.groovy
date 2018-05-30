def pipelinScript = "${WORKSPACE}/jobs/module_build_pipeline"

def disableProj = [
		"PolicyCenter": true,
        "BillingCenter": true,
        "ClaimCenter": true,
        "ContactManager": true
    ]

def list_projName = [
		"PolicyCenter",
		"BillingCenter",
		"ClaimCenter",
		"ContactManager"
    ]

def list_Version = [
        "2.7.1",
		"2.9",
		"2.10"
    ]

list_projName.eachWithIndex { projName, idx ->
    list_Version.eachWithIndex { listVersion, idx1 ->

    def jobName = "Test_${projName}ApplicationBuildR${listVersion}"
    def desc = "Job ${projName} is building with ${listVersion} version"

    pipelineJob(jobName) {

            description(desc)
            logRotator(30)
            disabled(disableProj[projName])

            throttleConcurrentBuilds {
                maxPerNode(1)
                maxTotal(2)
				//categories(["${projName}Build"])
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