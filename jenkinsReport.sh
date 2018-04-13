#!/bin/bash
set -o errexit
set -o nounset

jobName="${1:?enter job name}"
buildToCheck="${2:?enter number of builds to check}"
jenkinsURL="https://jenkins.cc.dev.aws.symcpe.com"
excelSheetName="sampleReport.csv"
success=0
failure=0
aborted=0

nextBuildNumber=$(curl -u ram_kannan:Sym#16info2020 --silent $jenkinsURL/job/$jobName/api/json?pretty=true | grep -e "nextBuildNumber" | cut -d : -f2 | tr -d ',' | xargs)

echo "Test Report running for last $buildToCheck builds" > $excelSheetName
echo "BUILD_NUMBER,BUILD_STATUS,BUILD_TIMESTAMP" >> $excelSheetName
for ((i=$((nextBuildNumber - buildToCheck));i<nextBuildNumber;i++))
{
BUILD_STATUS=$(curl -u ram_kannan:Sym#16info2020 --silent $jenkinsURL/job/$jobName/$i/api/json?pretty=true | grep -e "result" | cut -d : -f2 | tr -d ',' | xargs)
BUILD_TIMESTAMP=$(curl -u ram_kannan:Sym#16info2020 --silent $jenkinsURL/job/$jobName/$i/api/json?pretty=true | grep -e "result" -e "timestamp" | cut -d : -f2 | head -n 2 | tail -n +2 | tr -d ',' | xargs)
BUILD_TIMESTAMP=$(date -d @$BUILD_TIMESTAMP)
#BUILD_TIMESTAMP=$(date -r $BUILD_TIMESTAMP)
echo "$i,$BUILD_STATUS,$BUILD_TIMESTAMP " >> $excelSheetName
echo " " >> $excelSheetName
sleep 5
if [ "$BUILD_STATUS" == "SUCCESS" ]; then {
	success=$((success + 1))
} elif [ "$BUILD_STATUS" == "FAILURE" ]; then {
	failure=$((failure + 1))
} else {
	aborted=$((aborted + 1))
} 
fi
}

echo "*************************************************" >> $excelSheetName
echo "SUCCESS COUNT = $success" >> $excelSheetName
echo "FAILURE COUNT = $failure" >> $excelSheetName
echo "ABORTED or UNSTABLE COUNT = $aborted" >> $excelSheetName
echo "*************************************************" >> $excelSheetName
