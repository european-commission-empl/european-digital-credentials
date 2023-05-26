FROM tomcat:9.0-jdk11
WORKDIR $CATALINA_HOME
RUN useradd edci
RUN chown -R edci.edci /usr/local/tomcat
COPY edci-viewer-web/src/main/resources/config/docker/tomcat conf/
COPY edci-viewer-web/src/main/resources/config/docker/viewerStart.sh /opt/
COPY edci-viewer-web/src/main/resources/config/ext/conf/ /opt/edci/viewer
COPY edci-viewer-web/src/main/resources/config/ext/proxy/ /opt/edci/edci
COPY edci-viewer-web/src/main/resources/config/ext/shacl/ /opt/edci/edci
COPY edci-viewer-web/src/main/resources-unfiltered/lib lib/
COPY edci-viewer-web/target/europass2#edci-viewer.war /opt/europass2#edci-viewer.war
RUN chmod +x /opt/viewerStart.sh
USER edci
CMD ["/opt/viewerStart.sh"]

