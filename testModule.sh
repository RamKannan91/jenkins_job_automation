#! /bin/bash
cd /opt/fedex/srs/vre/
if [ ! -d "buildBackUp" ]; then
	mkdir buildBackUp
fi
lastSuccessFullBuildNumber = $(curl https://jenkins.prod.cloud.fedex.com:8443/jenkins/job/RAISE/job/VRService-3531606/job/VRS-Pipeline/job/master/lastSuccessfulBuild/artifact/build/distributions/ | grep -e id | cut -d ':' -f2 | tr -d ',' | xargs) 

if [ -e vre_${lastSuccessFullBuildNumber}.txt ]
then
    echo "file present"
    rm -rf vre_${lastSuccessFullBuildNumber}.txt
   	echo "stop"
else
    touch vre_${lastSuccessFullBuildNumber}.txt
fi


wget https://jenkins.prod.cloud.fedex.com:8443/jenkins/job/RAISE/job/VRService-3531606/job/VRS-Pipeline/job/master/lastSuccessfulBuild/artifact/build/distributions/srs-vre-app-62.0.srs_validation_rating_service-master-{lastSuccessFullBuildNumber}.linux.tar.gz
wget https://jenkins.prod.cloud.fedex.com:8443/jenkins/job/RAISE/job/VRService-3531606/job/VRS-Pipeline/job/master/lastSuccessfulBuild/artifact/build/distributions/srs-vre-var-62.0.srs_validation_rating_service-master-{lastSuccessFullBuildNumber}.linux.tar.gz
wget https://jenkins.prod.cloud.fedex.com:8443/jenkins/job/RAISE/job/VRService-3531606/job/VRS-Pipeline/job/master/lastSuccessfulBuild/artifact/build/distributions/srs-vre-app-62.0.vrs_IBR-master-{lastSuccessFullBuildNumber}.linux.tar.gz
wget https://jenkins.prod.cloud.fedex.com:8443/jenkins/job/RAISE/job/VRService-3531606/job/VRS-Pipeline/job/master/lastSuccessfulBuild/artifact/build/distributions/srs-vre-var-62.0.vrs_IBR-master-{lastSuccessFullBuildNumber}.linux.tar.gz

