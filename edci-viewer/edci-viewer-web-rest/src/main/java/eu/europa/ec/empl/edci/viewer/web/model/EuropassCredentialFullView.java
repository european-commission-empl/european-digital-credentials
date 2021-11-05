package eu.europa.ec.empl.edci.viewer.web.model;

public class EuropassCredentialFullView {

    private EuropassCredentialDetailView detail;
    private EuropassDiplomaView diploma;
    private String id;

    public EuropassCredentialDetailView getDetail() {
        return detail;
    }

    public void setDetail(EuropassCredentialDetailView detail) {
        this.detail = detail;
    }

    public EuropassDiplomaView getDiploma() {
        return diploma;
    }

    public void setDiploma(EuropassDiplomaView diploma) {
        this.diploma = diploma;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
