# docker build -t register:0.3.0-SNAP .
# Namensvorschlaege: register
#                    
# docker run --network betoffice --expose 8080 -p 9090:8080 --name register-0.2.0 -d register:0.2.0
#                             ^    ^- Port im Container
#                             \- Port im Host
#
# Im laufenden Betrieb ein WAR installieren:
# docker cp registrationservice-0.3.0-SNAPSHOT.war registerservice:/usr/local/tomcat/webapps/registrationservice.war
#

#
# docker network create dev
# docker network rm dev
# docker network connect dev mariadb
# docker network connect dev register
# Im JDBC connection string kann nun der Container Name, z.B. 'mariadb', als Host Name verwendet werden. 
#

# FROM tomcat:9.0.35-jdk14-openjdk-oracle
FROM tomcat:10.1.18-jdk21

LABEL maintainer="Andre Winkler"
LABEL mail="andre.winkler@web.de"

COPY registrationservice-0.4.0.war /usr/local/tomcat/webapps/registrationservice.war
COPY .register.properties /root/.register.properties

EXPOSE 8080
