#!/bin/bash

# Script to download and install dependencies to local repository
# This needs to be run once with internet access to populate local-repo

echo "Installing dependencies to local repository..."

# Create local repository directory if it doesn't exist
mkdir -p local-repo

# Install Maven plugins first
mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.apache.maven.plugins:maven-clean-plugin:3.2.0 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.apache.maven.plugins:maven-compiler-plugin:3.8.1 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.apache.maven.plugins:maven-war-plugin:3.2.3 \
    -Dmaven.repo.local=./local-repo

# Install project dependencies
mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=javax.servlet:javax.servlet-api:3.0.1 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=commons-lang:commons-lang:2.6 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=com.coverity.security:coverity-escapers:1.1 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=com.jayway.restassured:rest-assured:2.9.0 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.json:json:20190722 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.testng:testng:7.0.0 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.slf4j:slf4j-api:1.7.28 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.apache.logging.log4j:log4j-slf4j-impl:2.12.1 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.apache.logging.log4j:log4j-api:2.12.1 \
    -Dmaven.repo.local=./local-repo

mvn org.apache.maven.plugins:maven-dependency-plugin:3.2.0:get \
    -Dartifact=org.apache.logging.log4j:log4j-core:2.12.1 \
    -Dmaven.repo.local=./local-repo

echo "Dependencies installed successfully!"
echo "Now you can use 'mvn clean install -s maven-settings.xml' for offline builds"