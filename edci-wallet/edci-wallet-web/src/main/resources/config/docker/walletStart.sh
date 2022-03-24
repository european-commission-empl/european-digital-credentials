#!/bin/bash
if [ ! -f "/usr/local/tomcat/conf/edci/wallet/wallet.properties" ]
then
    mkdir /usr/local/tomcat/conf/edci
    mkdir /usr/local/tomcat/conf/edci/wallet
    cp -par /opt/edci/wallet /usr/local/tomcat/conf/edci/
fi
#if [ ! -f "/usr/local/tomcat/webapps/europass2#edci-wallet.war" ]
#then
cp /opt/europass2#edci-wallet.war /usr/local/tomcat/webapps/
cp /opt/europass2#edci-wallet-swaggerUI.war /usr/local/tomcat/webapps/
#fi
if [ ! -n "$WAIT_FOR_HOST" ] && [ ! -n "$WAIT_FOR_PORT" ] && [ ! -n "$WAIT_FOR_TIMEOUT" ]
then
    echo "Waiting for no one"
else
    bash /opt/wait-for-it.sh -h $WAIT_FOR_HOST -p $WAIT_FOR_PORT -t $WAIT_FOR_TIMEOUT
fi
if [ ! -n "JPDA_ENABLED" ] && [ ! "$JPDA_ENABLED" = "true"]
then
    echo "Starting tomcat..."
    /usr/local/tomcat/bin/catalina.sh run
else
    echo "Starting tomcat in debug mode. JPDA_ADDRESS: $JPDA_ADDRESS"
    /usr/local/tomcat/bin/catalina.sh jpda run
fi
