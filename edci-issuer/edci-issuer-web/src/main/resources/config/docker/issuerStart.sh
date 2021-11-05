#!/bin/bash
if [ ! -f "/usr/local/tomcat/conf/edci/issuer/issuer.properties" ]
then
    mkdir /usr/local/tomcat/conf/edci
    mkdir /usr/local/tomcat/conf/edci/issuer
    cp -par /opt/edci/issuer /usr/local/tomcat/conf/edci/
fi
#if [ ! -f "/usr/local/tomcat/webapps/europass2#edci-issuer.war" ]
#then
cp /opt/europass2#edci-issuer.war /usr/local/tomcat/webapps/
cp /opt/europass2#edci-issuer-swaggerUI.war /usr/local/tomcat/webapps/
#fi
if [ ! -n "$WAIT_FOR_HOST" ] && [ ! -n "$WAIT_FOR_PORT" ] && [ ! -n "$WAIT_FOR_TIMEOUT" ]
then
    echo "Waiting for no one"
else
    bash /opt/wait-for-it.sh -h $WAIT_FOR_HOST -p $WAIT_FOR_PORT -t $WAIT_FOR_TIMEOUT
fi
if [ ! -n "$JPDA_ADDRESS" ]
then
    echo "Starting tomcat..."
    /usr/local/tomcat/bin/catalina.sh run
else
    echo "Starting tomcat in debug mode. JPDA_ADDRESS: $JPDA_ADDRESS"
    /usr/local/tomcat/bin/catalina.sh jpda run
fi
