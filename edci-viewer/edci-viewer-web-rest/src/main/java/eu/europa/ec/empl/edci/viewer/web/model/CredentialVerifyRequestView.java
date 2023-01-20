package eu.europa.ec.empl.edci.viewer.web.model;

import javax.validation.constraints.NotNull;

public class CredentialVerifyRequestView {
    @NotNull
    private String uuid;
    private String firstName;
    private String lastName;
    private String birthDate;

    public CredentialVerifyRequestView() {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

}