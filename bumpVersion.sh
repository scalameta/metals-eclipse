#!/bin/bash

ARGV=("$@")
ARGC=("$#")

RELEASE_VERSION=${ARGV[0]}
NEW_VERSION=${ARGV[1]}

if [ $ARGC -eq 2 ]; then
  cd metals-eclipse/parent
  mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$RELEASE_VERSION -f parent/pom.xml
  git commit -a -m "Bump version to $NEW_VERSION"
  git tag -a v$RELEASE_VERSION
  git push --tags --force origin master
  mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$NEW_VERSION -f parent/pom.xml 
  git commit -a -m "Bump version to $NEW_VERSION-SNAPSHOT"
  git push origin master
else
  printf "\nInvalid number of arguments"
  printf "\nUsage: ./bumVersion.sh <releaseVersion> <newVersion>"
fi;

