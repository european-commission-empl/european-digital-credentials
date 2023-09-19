package eu.europa.ec.empl.edci.model;

public class AppDetails {

    private String appVersion;

    public AppDetails(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
