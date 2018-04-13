#!/bin/bash
set -o errexit
set -o nounset

jobName="${1:?enter job name}"
jenkinsURL="https://jenkins.cc.dev.aws.symcpe.com"
excelSheetName="report.csv"

echo "COMMIT_ID,,COMMIT_AUTHOR_NAME,,COMMIT_MSG" > $excelSheetName

echo "COMMIT_ID"
commitID="$(curl -u ram_kannan:Sym#16info2020 --silent $jenkinsURL/job/$jobName/lastBuild/api/json?pretty=true | grep -e commitId | cut -d : -f2)" 
echo "$commitID" > commitID.txt
echo "*************************************************" 

wordCound=$(wc -l < commitID.txt | xargs)
echo "No of commits == $wordCound" 

echo "COMMIT_AUTHOR_NAME" 
COMMIT_AUTHOR_NAME="$(curl -u ram_kannan:Sym#16info2020 --silent $jenkinsURL/job/$jobName/lastBuild/api/json?pretty=true | grep -e "fullName" | cut -d : -f2 | head -n $wordCound)"
echo "$COMMIT_AUTHOR_NAME" > COMMIT_AUTHOR_NAME.txt
echo "*************************************************" 

echo "COMMIT_MSG" 
COMMIT_MSG="$(curl -u ram_kannan:Sym#16info2020 --silent $jenkinsURL/job/$jobName/lastBuild/api/json?pretty=true | grep -e "msg" | cut -d : -f2)"
echo "$COMMIT_MSG" > COMMIT_MSG.txt
echo "*************************************************" 



paste -d',' commitID.txt COMMIT_MSG.txt COMMIT_AUTHOR_NAME.txt >> $excelSheetName