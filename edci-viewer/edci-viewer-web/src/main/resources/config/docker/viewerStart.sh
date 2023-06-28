#!/bin/bash
if [ ! -f "/usr/local/tomcat/conf/edci/viewer/viewer.properties" ]
then
    mkdir /usr/local/tomcat/conf/edci
    mkdir /usr/local/tomcat/conf/edci/viewer
    cp -par /opt/edci/viewer /usr/local/tomcat/conf/edci/
fi

if [ ! -f "/usr/local/tomcat/conf/edci/shacl.properties" ]
then
   cp  /opt/edci/edci/shacl.properties /usr/local/tomcat/conf/edci/

fi

if [ ! -f "/usr/local/tomcat/conf/edci/proxy.properties" ]
then
    cp  /opt/edci/edci/proxy.properties /usr/local/tomcat/conf/edci/
fi 

cp /opt/europass2#edci-viewer.war /usr/local/tomcat/webapps/

if [ ! -n "$JPDA_ADDRESS" ]
then        
    echo "Starting tomcat..."
    /usr/local/tomcat/bin/catalina.sh run
else
    echo "Starting tomcat in debug mode. JPDA_ADDRESS: $JPDA_ADDRESS"
    /usr/local/tomcat/bin/catalina.sh jpda run
fi
