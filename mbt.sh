#!/bin/bash

MAVEN_REPOSITORY=$HOME/.m2/repository

CP=$MAVEN_REPOSITORY/commons-lang/commons-lang/1.0/commons-lang-1.0.jar
CP=$CP:$MAVEN_REPOSITORY/commons-cli/commons-cli/1.0/commons-cli-1.0.jar
CP=$CP:$MAVEN_REPOSITORY/commons-logging/commons-logging/1.0/commons-logging-1.0.jar
CP=$CP:$MAVEN_REPOSITORY/log4j/log4j/1.2.12/log4j-1.2.12.jar
CP=$CP:target/classes
#CP=$CP:target/MBeanToucher-1.0-SNAPSHOT.jar

JAVA_PROPS=-DsocksProxyHost=localhost

java -cp $CP $JAVA_PROPS com.vocel.jmx.MBeanToucher "$@"

