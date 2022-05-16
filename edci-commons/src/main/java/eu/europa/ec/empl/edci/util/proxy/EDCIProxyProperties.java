package eu.europa.ec.empl.edci.util.proxy;

public class EDCIProxyProperties {
    private String host;
    private int port;
    private String user;
    private String password;
    private String excludedHosts;

    public EDCIProxyProperties() {
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExcludedHosts() {
        return this.excludedHosts;
    }

    public void setExcludedHosts(String excludedHosts) {
        this.excludedHosts = excludedHosts;
    }
}