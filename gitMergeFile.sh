#!/bin/bash
set -o errexit
set -o nounset

branch_list="files/branchList.list"
git_url="https://....."

mkdir test
cd test/

while IFS= read -r var
do
	echo "branch name == $var"
	mkdir file_$var
	cd file_$var
	git clone -b $var $git_url
	git checkout -b feature/test_$var
	cp /tmp/Jenkinsfile .
	git add Jenkinsfile
	git commit -m "adding Jenkinsfile to branch $var"
	git push --set-upstream origin feature/test_$var
	cd ..
done < "$branch_list"
