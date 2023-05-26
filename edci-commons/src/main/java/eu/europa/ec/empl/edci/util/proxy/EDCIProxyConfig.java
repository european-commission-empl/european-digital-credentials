package eu.europa.ec.empl.edci.util.proxy;

public class EDCIProxyConfig {
    private boolean httpEnabled;
    private boolean httpsEnabled;
    private EDCIProxyProperties httpProperties;
    private EDCIProxyProperties httpsProperties;

    public EDCIProxyConfig() {
    }

    public boolean isHttpEnabled() {
        return httpEnabled;
    }

    public void setHttpEnabled(boolean httpEnabled) {
        this.httpEnabled = httpEnabled;
    }

    public boolean isHttpsEnabled() {
        return httpsEnabled;
    }

    public void setHttpsEnabled(boolean httpsEnabled) {
        this.httpsEnabled = httpsEnabled;
    }

    public EDCIProxyProperties getHttpProperties() {
        return this.httpProperties;
    }

    public void setHttpProperties(EDCIProxyProperties httpProperties) {
        this.httpProperties = httpProperties;
    }

    public EDCIProxyProperties getHttpsProperties() {
        return this.httpsProperties;
    }

    public void setHttpsProperties(EDCIProxyProperties httpsProperties) {
        this.httpsProperties = httpsProperties;
    }
}
