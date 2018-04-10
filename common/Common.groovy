package common

class Common {
    def static svn_creds = 'svn-jenkins-creds'

    def static list_projName = [
            "PolicyCenter",
            "BillingCenter",
			"ClaimCenter",
			"ContactManager"
    ]

    def static list_Version = [
            "Release_2.6",
            "Release_2.7.1",
            "Release_2.7"
    ]

    def static projectVersion = [
        "AccountManagementPortal": "Release_2.6",
        "BillingCenter"     : "Release_2.7.1"
    ]

}
