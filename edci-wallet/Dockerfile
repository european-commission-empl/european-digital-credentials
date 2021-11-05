FROM tomcat:9.0-jdk8
WORKDIR $CATALINA_HOME
COPY edci-wallet-web/src/main/resources/config/docker/tomcat conf/
COPY edci-wallet-web/src/main/resources/config/docker/walletStart.sh /opt/
COPY edci-wallet-web/src/main/resources/config/docker/wait-for-it.sh /opt/
COPY edci-wallet-web/src/main/resources/config/ext/conf/ /opt/edci/wallet
COPY edci-wallet-web/src/main/resources-unfiltered/lib lib/
COPY edci-wallet-web/target/europass2#edci-wallet.war /opt/europass2#edci-wallet.war
COPY edci-wallet-web-swagger-ui/target/europass2#edci-wallet-swaggerUI.war /opt/europass2#edci-wallet-swaggerUI.war
RUN chmod +x /opt/walletStart.sh
RUN chmod +x /opt/wait-for-it.sh
CMD ["/opt/walletStart.sh"]
