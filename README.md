[![Build
Status](https://travis-ci.org/scalameta/metals-eclipse.svg?branch=master)](https://travis-ci.org/scalameta/metals-eclipse)


# metals-eclipse

Eclipse Scala LSP plugin for Metals (WIP)

# Build

To build:

    $ git clone https://github.com/scalameta/metals-eclipse.git
    $ cd metals-eclipse/parent
    $ mvn clean install

# Release procedure

Do the following steps:

    $ cd metals-eclipse/parent
    $ mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=<release version>
    $ git commit -a -m "Bump version to <release version>"
    $ git tag -a v< version>
    $ git push --tags origin master
    $ mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=<new version>-SNAPSHOT
    $ git commit -a -m "Bump version to <new version>-SNAPSHOT"
    $ git push origin master

The new version will automatically be pushed to the repository and be available
to download.
