
def pipelinScript = "appBuild"

def disableProj = ["PolicyCenter": true,
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
        "Release_2.6",
        "Release_2.7.1"
    ]

list_projName.eachWithIndex { projName, idx ->
    list_Version.eachWithIndex { listVersion, idx1 ->

    def jobName = "Test_${projName} - Build ${listVersion}"
    def desc = "Job ${projName} is building with ${listVersion} version"

    pipelineJob(jobName) {

            description(desc)
            logRotator(30)
            disabled(disableProj[projName])

            throttleConcurrentBuilds {
                maxPerNode(1)
                maxTotal(2)
                categories(['cat-1'])
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