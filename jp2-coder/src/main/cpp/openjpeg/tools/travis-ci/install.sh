#!/bin/bash

# This script executes the install step when running under travis-ci

#if cygwin, check path
case ${MACHTYPE} in
	*cygwin*) OPJ_CI_IS_CYGWIN=1;;
	*) ;;
esac

if [ "${OPJ_CI_IS_CYGWIN:-}" == "1" ]; then
	# PATH is not yet set up
	export PATH=$PATH:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
fi

# Set-up some error handling
set -o nounset   ## set -u : exit the script if you try to use an uninitialised variable
set -o errexit   ## set -e : exit the script if any statement returns a non-true return value
set -o pipefail  ## Fail on error in pipe

function exit_handler ()
{
	local exit_code="$?"
	
	test ${exit_code} == 0 && return;

	echo -e "\nInstall failed !!!\nLast command at line ${BASH_LINENO}: ${BASH_COMMAND}";
	exit "${exit_code}"
}
trap exit_handler EXIT
trap exit ERR

# We don't need anything for coverity scan builds. ABI check is managed in abi-check.sh
if [ "${COVERITY_SCAN_BRANCH:-}" == "1" ] || [ "${OPJ_CI_ABI_CHECK:-}" == "1" ]; then
	exit 0
fi

if [ "${OPJ_CI_ASAN:-}" == "1" ]; then
	# We need a new version of cmake than travis-ci provides
	wget --no-check-certificate -qO - https://cmake.org/files/v3.5/cmake-3.5.2-Linux-x86_64.tar.gz | tar -xz
	# copy to a directory that will not changed every version
	mv cmake-3.5.2-Linux-x86_64 cmake-install
fi

if [ "${OPJ_CI_SKIP_TESTS:-}" != "1" ]; then

	OPJ_SOURCE_DIR=$(cd $(dirname $0)/../.. && pwd)

	# We need test data
	if [ "${TRAVIS_BRANCH:-}" != "" ]; then
		OPJ_DATA_BRANCH=${TRAVIS_BRANCH}
	elif [ "${APPVEYOR_REPO_BRANCH:-}" != "" ]; then
		OPJ_DATA_BRANCH=${APPVEYOR_REPO_BRANCH}
	else
		OPJ_DATA_BRANCH=$(git -C ${OPJ_SOURCE_DIR} branch | grep '*' | tr -d '*[[:blank:]]') #default to same branch as we're setting up
	fi
	OPJ_DATA_HAS_BRANCH=$(git ls-remote --heads git://github.com/uclouvain/openjpeg-data.git ${OPJ_DATA_BRANCH} | wc -l)
	if [ ${OPJ_DATA_HAS_BRANCH} -eq 0 ]; then
		OPJ_DATA_BRANCH=master #default to master
	fi
	echo "Cloning openjpeg-data from ${OPJ_DATA_BRANCH} branch"
	git clone --depth=1 --branch=${OPJ_DATA_BRANCH} git://github.com/uclouvain/openjpeg-data.git data

	# We need jpylyzer for the test suite
    JPYLYZER_VERSION="1.17.0"    
	echo "Retrieving jpylyzer"
	if [ "${APPVEYOR:-}" == "True" ]; then
		wget -q http://dl.bintray.com/openplanets/opf-windows/jpylyzer_${JPYLYZER_VERSION}_win32.zip
		mkdir jpylyzer
		cd jpylyzer
		cmake -E tar -xf ../jpylyzer_${JPYLYZER_VERSION}_win32.zip
		cd ..
	else
		wget -qO - https://github.com/openpreserve/jpylyzer/archive/${JPYLYZER_VERSION}.tar.gz | tar -xz
		mv jpylyzer-${JPYLYZER_VERSION}/jpylyzer ./
		chmod +x jpylyzer/jpylyzer.py
	fi

	# When OPJ_NONCOMMERCIAL=1, kakadu trial binaries are used for testing. Here's the copyright notice from kakadu:
	# Copyright is owned by NewSouth Innovations Pty Limited, commercial arm of the UNSW Australia in Sydney.
	# You are free to trial these executables and even to re-distribute them, 
	# so long as such use or re-distribution is accompanied with this copyright notice and is not for commercial gain.
	# Note: Binaries can only be used for non-commercial purposes.
	if [ "${OPJ_NONCOMMERCIAL:-}" == "1" ]; then
		if [ "${TRAVIS_OS_NAME:-}" == "linux" ] || uname -s | grep -i Linux &> /dev/null; then
			echo "Retrieving Kakadu"
			wget -q http://kakadusoftware.com/wp-content/uploads/2014/06/KDU77_Demo_Apps_for_Linux-x86-64_150710.zip
			cmake -E tar -xf KDU77_Demo_Apps_for_Linux-x86-64_150710.zip
			mv KDU77_Demo_Apps_for_Linux-x86-64_150710 kdu
		elif [ "${TRAVIS_OS_NAME:-}" == "osx" ] || uname -s | grep -i Darwin &> /dev/null; then
			echo "Retrieving Kakadu"
			wget -v http://kakadusoftware.com/wp-content/uploads/2014/06/KDU77_Demo_Apps_for_OSX109_150710.dmg_.zip
			cmake -E tar -xf KDU77_Demo_Apps_for_OSX109_150710.dmg_.zip
			wget -q http://downloads.sourceforge.net/project/catacombae/HFSExplorer/0.23/hfsexplorer-0.23-bin.zip
			mkdir hfsexplorer && cmake -E chdir hfsexplorer tar -xf ../hfsexplorer-0.23-bin.zip
			./hfsexplorer/bin/unhfs.sh -o ./ -fsroot Kakadu-demo-apps.pkg  KDU77_Demo_Apps_for_OSX109_150710.dmg
			pkgutil --expand Kakadu-demo-apps.pkg ./kdu
			cd kdu
			cat libkduv77r.pkg/Payload | gzip -d | cpio -id
			cat kduexpand.pkg/Payload | gzip -d | cpio -id
			cat kducompress.pkg/Payload | gzip -d | cpio -id
			install_name_tool -id ${PWD}/libkdu_v77R.dylib libkdu_v77R.dylib 
			install_name_tool -change /usr/local/lib/libkdu_v77R.dylib ${PWD}/libkdu_v77R.dylib kdu_compress
			install_name_tool -change /usr/local/lib/libkdu_v77R.dylib ${PWD}/libkdu_v77R.dylib kdu_expand
		elif [ "${APPVEYOR:-}" == "True" ] || uname -s | grep -i MINGW &> /dev/null || uname -s | grep -i CYGWIN &> /dev/null; then
			echo "Retrieving Kakadu"
			wget -q http://kakadusoftware.com/wp-content/uploads/2014/06/KDU77_Demo_Apps_for_Win32_150710.msi_.zip
			cmake -E tar -xf KDU77_Demo_Apps_for_Win32_150710.msi_.zip
			msiexec /i KDU77_Demo_Apps_for_Win32_150710.msi /quiet /qn /norestart
			if [ -d "C:/Program Files/Kakadu" ]; then
				cp -r "C:/Program Files/Kakadu" ./kdu
			else
				cp -r "C:/Program Files (x86)/Kakadu" ./kdu
			fi
		fi
	fi
fi

if [ "${OPJ_CI_CHECK_STYLE:-}" == "1" ]; then
    pip install --user autopep8
fi
