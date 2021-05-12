package eu.europa.ec.empl.edci.viewer.common.constants;

public class EDCIViewerConfig {

    public class Path {
        public static final String SECURITY_FILE = "file:${catalina.base}/conf/edci/viewer/security.properties";
        public static final String VIEWER_FILE = "file:${catalina.base}/conf/edci/viewer/viewer.properties";
    }
}
