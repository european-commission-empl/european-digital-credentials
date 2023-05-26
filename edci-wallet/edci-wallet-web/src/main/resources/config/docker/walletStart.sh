#!/bin/bash
if [ ! -f "/usr/local/tomcat/conf/edci/wallet/wallet.properties" ]
then
    mkdir /usr/local/tomcat/conf/edci
    mkdir /usr/local/tomcat/conf/edci/wallet
    cp -par /opt/edci/wallet /usr/local/tomcat/conf/edci/
fi

if [ ! -f "/usr/local/tomcat/conf/edci/shacl.properties" ]
then
   cp  /opt/edci/edci/shacl.properties /usr/local/tomcat/conf/edci/

fi

if [ ! -f "/usr/local/tomcat/conf/edci/proxy.properties" ]
then
    cp  /opt/edci/edci/proxy.properties /usr/local/tomcat/conf/edci/
fi 

cp /opt/europass2#edci-wallet.war /usr/local/tomcat/webapps/
cp /opt/europass2#edci-wallet-swaggerUI.war /usr/local/tomcat/webapps/

if [ ! -n "JPDA_ENABLED" ] && [ ! "$JPDA_ENABLED" = "true"]
then
    echo "Starting tomcat..."
    /usr/local/tomcat/bin/catalina.sh run
else
    echo "Starting tomcat in debug mode. JPDA_ADDRESS: $JPDA_ADDRESS"
    /usr/local/tomcat/bin/catalina.sh jpda run
fi
