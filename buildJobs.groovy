import common.Common

def pipelinScript = "jobs/Build_Applications"

def disableProj = ["AccountManagementPortal": true,
                   "BillingCenter"     : true
                   ]

Common.list_projName.eachWithIndex { projName, idx ->
    Common.list_Version.eachWithIndex { listVersion, idx1 ->

    def jobName = "Test_${Common.projName} - Build ${Common.listVersion}}"
    def desc = """
    Job ${projName} is building with ${Common.listVersion}} version
    """

    pipelineJob(jobName) {

            description(desc)
            logRotator(30)
            disabled(disableProj[projName])

            throttleConcurrentBuilds {
                maxPerNode(1)
                maxTotal(2)
                categories(['cat-1'])
            }
            
            scm { //to be edited URl of project
                svn {
                    location('https://gsvnpapp1.germania-ins.com/svn/guidewire/applications/branches/Release_2.7.1/BillingCenter/modules/configuration@HEAD') {
                        directory('BillingCenter/Module/configuration')
                        depth(SvnDepth.INFINITY)
                    }
                }
                svn {
                    location('https://gsvnpapp1.germania-ins.com/svn/guidewire/applications/branches/Release_2.7.1/PolicyCenter/modules/configuration@HEAD') {
                        directory('ext_temp/PolicyCenter/Module/configuration')
                        depth(SvnDepth.INFINITY)
                    }
                }
            }

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