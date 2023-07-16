#!/usr/bin/env bash
###########################################################################
#    prepare-commit.sh
#    ---------------------
#    Date                 : August 2008
#    Copyright            : (C) 2008 by Juergen E. Fischer
#    Email                : jef at norbit dot de
###########################################################################
#                                                                         #
#   This program is free software; you can redistribute it and/or modify  #
#   it under the terms of the GNU General Public License as published by  #
#   the Free Software Foundation; either version 2 of the License, or     #
#   (at your option) any later version.                                   #
#                                                                         #
###########################################################################

TOPLEVEL=$(git rev-parse --show-toplevel)

PATH=$TOPLEVEL/scripts:$PATH

cd $TOPLEVEL

# GNU prefix command for mac os support (gsed, gsplit)
GP=
if [[ "$OSTYPE" =~ darwin* ]]; then
  GP=g
fi

if ! type -p astyle.sh >/dev/null; then
  echo astyle.sh not found
  exit 1
fi

if ! type -p colordiff >/dev/null; then
  colordiff()
  {
    cat "$@"
  }
fi

if [ "$1" = "-c" ]; then
  echo "Cleaning..."
  remove_temporary_files.sh
fi

set -e

# determine changed files
MODIFIED=$(git status --porcelain| ${GP}sed -ne "s/^ *[MA]  *//p" | sort -u)
#MODIFIED=$(find src -name "*.h")

if [ -z "$MODIFIED" ]; then
  echo nothing was modified
  exit 0
fi

# save original changes
REV=$(git log -n1 --pretty=%H)
git diff >sha-$REV.diff

ASTYLEDIFF=astyle.$REV.diff
>$ASTYLEDIFF

# reformat
i=0
N=$(echo $MODIFIED | wc -w)
for f in $MODIFIED; do
  (( i++ )) || true

  case "$f" in
  thirdparty/*)
    echo $f skipped
    continue
    ;;

  *.cpp|*.c|*.h|*.cxx|*.hxx|*.c++|*.h++|*.cc|*.hh|*.C|*.H|*.sip|*.py)
    ;;

  *)
    continue
    ;;
  esac

  m=$f.$REV.prepare

  cp $f $m
  ASTYLEPROGRESS=" [$i/$N]" astyle.sh $f
  if diff -u $m $f >>$ASTYLEDIFF; then
    # no difference found
    rm $m
  fi
done

if [ -s "$ASTYLEDIFF" ]; then
  if tty -s; then
    # review astyle changes
    colordiff <$ASTYLEDIFF | less -r
  else
    echo "Files changed (see $ASTYLEDIFF)"
  fi
  exit 1
else
  rm $ASTYLEDIFF
fi


# If there are whitespace errors, print the offending file names and fail.
exec git diff-index --check --cached HEAD --

exit 0

# vim: set ts=8 noexpandtab :
