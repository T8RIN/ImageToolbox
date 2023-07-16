#!/bin/bash

# This script executes the abi-check step when running under travis-ci (in run step)

# Set-up some bash options
set -o nounset   ## set -u : exit the script if you try to use an uninitialised variable
set -o errexit   ## set -e : exit the script if any statement returns a non-true return value
set -o pipefail  ## Fail on error in pipe
set -o xtrace    ## set -x : Print a trace of simple commands and their arguments after they are expanded and before they are executed.

# Exit if not ABI check
if [ "${OPJ_CI_ABI_CHECK:-}" != "1" ]; then
	exit 0
fi

if [ "${OPJ_CI_CC:-}" != "" ]; then
    export CC=${OPJ_CI_CC}
    echo "Using ${CC}"
fi

if [ "${OPJ_CI_CXX:-}" != "" ]; then
    export CXX=${OPJ_CI_CXX}
    echo "Using ${CXX}"
fi

OPJ_UPLOAD_ABI_REPORT=0
#OPJ_PREVIOUS_VERSION="2.3.0"
OPJ_LATEST_VERSION="2.3.1"
if [ "${OPJ_PREVIOUS_VERSION:-}" != "" ]; then
	OPJ_LIMIT_ABI_BUILDS="-limit 3"
else
	OPJ_LIMIT_ABI_BUILDS="-limit 2"
fi
OPJ_REPO="https://github.com/uclouvain/openjpeg.git"
OPJ_SSH_REPO=${OPJ_REPO/https:\/\/github.com\//git@github.com:}
OPJ_UPLOAD_BRANCH="gh-pages"
OPJ_UPLOAD_DIR="abi-check"
if [ "${TRAVIS_REPO_SLUG:-}" != "" ]; then
	if [ "$(echo "${TRAVIS_REPO_SLUG}" | sed 's/\(^.*\)\/.*/\1/')" == "uclouvain" ] && [ "${TRAVIS_PULL_REQUEST:-}" == "false" ] && [ "${TRAVIS_BRANCH:-}" == "master" ]; then
		# Upload updated report to gh-pages
		OPJ_UPLOAD_ABI_REPORT=1
		# Build full report
		#OPJ_LIMIT_ABI_BUILDS=
	fi
fi

OPJ_SOURCE_DIR=$(cd $(dirname $0)/../.. && pwd)

# INSTALL REQUIRED PACKAGES

mkdir ${HOME}/abi-check
cd ${HOME}/abi-check
# Let's get tools not available with apt
mkdir tools
# Travis doesn't allow package wdiff...
wget -qO - http://mirrors.kernel.org/gnu/wdiff/wdiff-latest.tar.gz | tar -xz
cd wdiff-*
./configure --prefix=${HOME}/abi-check/tools/wdiff &> /dev/null
make &> /dev/null
make check &> /dev/null
make install &> /dev/null
cd ..
export PATH=${PWD}/tools/wdiff/bin:$PATH
wget https://tools.ietf.org/tools/rfcdiff/rfcdiff
chmod +x rfcdiff
mv rfcdiff ${PWD}/tools
export PATH=${PWD}/tools:$PATH
wget -qO - https://github.com/lvc/installer/archive/0.10.tar.gz | tar -xz
mkdir ${PWD}/tools/abi-tracker
make -C installer-0.10 install prefix=${PWD}/tools/abi-tracker target=abi-tracker
export PATH=${PWD}/tools/abi-tracker/bin:$PATH

# This will print configuration
# travis-ci doesn't dump cmake version in system info, let's print it 
cmake --version

# RUN THE ABI-CHECK SCRIPTS

mkdir work
cd work

# Clone the gh-pages branch and work from there
git clone -b $OPJ_UPLOAD_BRANCH --single-branch $OPJ_REPO .
cd $OPJ_UPLOAD_DIR
rm -rf installed/openjpeg/current/*

# Let's create all we need
grep -v Git ${OPJ_SOURCE_DIR}/tools/abi-tracker/openjpeg.json > ./openjpeg.json
abi-monitor ${OPJ_LIMIT_ABI_BUILDS} -get openjpeg.json
if [ "${OPJ_LIMIT_ABI_BUILDS}" != "" ]; then
	cp -f ${OPJ_SOURCE_DIR}/tools/abi-tracker/openjpeg.json ./openjpeg.json
else
	# Old versions of openjpeg don't like -fvisibility=hidden...
	grep -v Configure ${OPJ_SOURCE_DIR}/tools/abi-tracker/openjpeg.json > ./openjpeg.json
fi
cp -rf ${OPJ_SOURCE_DIR} src/openjpeg/current
abi-monitor -v current -build openjpeg.json

rm -rf ./installed/openjpeg/${OPJ_LATEST_VERSION}
rm -rf ./compat_report/openjpeg/${OPJ_LATEST_VERSION}
rm -rf ./abi_dump/openjpeg/${OPJ_LATEST_VERSION}
rm -rf ./headers_diff/openjpeg/${OPJ_LATEST_VERSION}
rm -rf ./objects_report/openjpeg/${OPJ_LATEST_VERSION}
abi-monitor -v ${OPJ_LATEST_VERSION} -build openjpeg.json
if [ "${OPJ_PREVIOUS_VERSION:-}" != "" ]; then
	abi-monitor -v ${OPJ_PREVIOUS_VERSION} -build openjpeg.json
fi
abi-tracker -build openjpeg.json

EXIT_CODE=0

# Check API
abi-compliance-checker -l openjpeg -old $(find ./abi_dump/openjpeg/$OPJ_LATEST_VERSION -name '*.dump') -new $(find ./abi_dump/openjpeg/current -name '*.dump') -header openjpeg.h -api -s || EXIT_CODE=1
if [ "${OPJ_PREVIOUS_VERSION:-}" != "" ]; then
	abi-compliance-checker -l openjpeg -old $(find ./abi_dump/openjpeg/$OPJ_PREVIOUS_VERSION -name '*.dump') -new $(find ./abi_dump/openjpeg/$OPJ_LATEST_VERSION -name '*.dump') -header openjpeg.h -api -s || EXIT_CODE=1
fi

# Check ABI
if [ "${OPJ_LIMIT_ABI_BUILDS}" != "" ]; then
	abi-compliance-checker -l openjpeg -old $(find ./abi_dump/openjpeg/$OPJ_LATEST_VERSION -name '*.dump') -new $(find ./abi_dump/openjpeg/current -name '*.dump') -header openjpeg.h -abi -s || EXIT_CODE=1
        if [ ${EXIT_CODE} -eq 1 ]; then
            cat "compat_reports/openjpeg/${OPJ_LATEST_VERSION}_to_current/abi_compat_report.html"
        fi
	if [ "${OPJ_PREVIOUS_VERSION:-}" != "" ]; then
		abi-compliance-checker -l openjpeg -old $(find ./abi_dump/openjpeg/$OPJ_PREVIOUS_VERSION -name '*.dump') -new $(find ./abi_dump/openjpeg/$OPJ_LATEST_VERSION -name '*.dump') -header openjpeg.h -abi -s || EXIT_CODE=1
	fi
else
	echo "Disable ABI check for now, problems with symbol visibility..."
fi

rm -rf src/openjpeg/current
rm -rf build_logs
	
if [ ${OPJ_UPLOAD_ABI_REPORT} -eq 1 ]; then
	git config user.name "OpenJPEG Travis CI"
	git config user.email "info@openjpeg.org"

	git add --all .
	git commit -m "Update ABI/API compatibility reports after commit ${TRAVIS_COMMIT:-}"

	# Get the deploy key by using Travis's stored variables to decrypt travis_rsa.enc
	openssl aes-256-cbc -K $encrypted_99d63218f67a_key -iv $encrypted_99d63218f67a_iv -in ${OPJ_SOURCE_DIR}/tools/travis-ci/travis_rsa.enc -out travis_rsa -d
	chmod 600 travis_rsa
	eval `ssh-agent -s`
	ssh-add travis_rsa

	# Now that we're all set up, we can push.
	git push $OPJ_SSH_REPO $OPJ_UPLOAD_BRANCH
fi

rm -rf src installed

exit $EXIT_CODE
