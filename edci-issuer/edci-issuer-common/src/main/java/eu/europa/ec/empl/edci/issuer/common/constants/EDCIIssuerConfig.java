package eu.europa.ec.empl.edci.issuer.common.constants;

public class EDCIIssuerConfig {

    public class Path {
        public static final String SECURITY_FILE = "file:${catalina.base}/conf/edci/issuer/security.properties";
        public static final String ISSUER_FILE = "file:${catalina.base}/conf/edci/issuer/issuer.properties";
        public static final String MAIL_FILE = "file:${catalina.base}/conf/edci/issuer/mail.properties";
        public static final String PROXY_FILE = "file:${catalina.base}/conf/edci/issuer/proxy.properties";
        public static final String ISSUER_DSS_FILE = "file:${catalina.base}/conf/edci/issuer/issuer_dss.properties";
    }
}
