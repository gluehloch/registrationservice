#!/bin/bash

# mvn package -DskipTests && docker cp ./target/registrationservice-0.0.1-SNAPSHOT.war register-0.0.1:/usr/local/tomcat/webapps/registrationservice.war

mvn package && cp ./target/registrationservice-0.0.1-SNAPSHOT.war /cygdrive/c/development/devtools/tomcat/apache-tomcat-9.0.14/webapps/registrationservice.war
