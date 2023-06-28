FROM tomcat:9.0-jdk11
WORKDIR $CATALINA_HOME
RUN useradd edci
RUN chown -R edci.edci /usr/local/tomcat
COPY edci-wallet-web/src/main/resources/config/docker/tomcat conf/
COPY edci-wallet-web/src/main/resources/config/docker/walletStart.sh /opt/
COPY edci-wallet-web/src/main/resources/config/ext/conf/ /opt/edci/wallet
COPY edci-wallet-web/src/main/resources/config/ext/shacl/ /opt/edci/edci
COPY edci-wallet-web/src/main/resources/config/ext/proxy/ /opt/edci/edci
COPY edci-wallet-web/src/main/resources-unfiltered/lib lib/
COPY edci-wallet-web/target/europass2#edci-wallet.war /opt/europass2#edci-wallet.war
COPY edci-wallet-web-swagger-ui/target/europass2#edci-wallet-swaggerUI.war /opt/europass2#edci-wallet-swaggerUI.war
RUN chmod +x /opt/walletStart.sh
USER edci
CMD ["/opt/walletStart.sh"]
