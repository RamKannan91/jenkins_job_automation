node {
   stage ("test-tag") {
        build_name = buildHistory(currentBuild.getPreviousBuild())
        currentBuild.displayName = "$build_name"
    }
}

def buildHistory(build) {
    def tagVersion=5.2
    if(build != null) {
        if (build.result != 'FAILURE') {
            t = build.displayName
            println "---- t = $t"
            String count = sh(returnStdout: true, script: """ echo $t | tr -dc '.' | wc -c | xargs """).trim()
            if (count > 1) {
                String tagPartVer = sh(returnStdout: true, script: """
                    newt=\$(echo $t | cut -d '.' -f1)'.'\$(echo $t | cut -d '.' -f2)
                    echo \$newt + 0.1 | bc
                """).trim()
                tagVersion = "$tagPartVer"
              } else {
              String tagPartVer = sh(returnStdout: true, script: """
                  echo $t + 0.1 | bc
              """).trim()
              tagVersion = "$tagPartVer"
            }
            println "---- on success-- $tagVersion"
        } else {
            t = build.displayName
            println "---- t = $t"
            String tagFirstVer = sh(returnStdout: true, script: """
                td=\$(echo $t | cut -d '.' -f1)
                echo \$td
            """).trim()
            String tagPartVer = sh(returnStdout: true, script: """
                td=\$(echo $t | cut -d '.' -f2)
                echo \$td + 0.1 | bc
            """).trim()
            println "----- tagpart = $tagFirstVer ^ $tagPartVer"
            tagVersion = "$tagFirstVer.$tagPartVer"
            println "----on failure-- $tagVersion"
        }
    }
    return tagVersion
}
