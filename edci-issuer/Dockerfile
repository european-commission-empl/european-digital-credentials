FROM tomcat:9.0-jdk8
WORKDIR $CATALINA_HOME
RUN useradd edci
RUN chown -R edci.edci /usr/local/tomcat
COPY edci-issuer-web/src/main/resources/config/docker/tomcat conf/
COPY edci-issuer-web/src/main/resources/config/docker/issuerStart.sh /opt/
COPY edci-issuer-web/src/main/resources/config/ext/conf/ /opt/edci/issuer
COPY edci-issuer-web/src/main/resources-unfiltered/lib lib/
COPY edci-issuer-web/target/europass2#edci-issuer.war /opt/europass2#edci-issuer.war
COPY edci-issuer-web-swagger-ui/target/europass2#edci-issuer-swaggerUI.war /opt/europass2#edci-issuer-swaggerUI.war
RUN chmod +x /opt/issuerStart.sh
USER edci
CMD ["/opt/issuerStart.sh"]

