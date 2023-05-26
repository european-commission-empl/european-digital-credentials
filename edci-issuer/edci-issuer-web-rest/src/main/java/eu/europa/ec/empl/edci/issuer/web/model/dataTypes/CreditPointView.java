package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class CreditPointView extends DataTypeView {

    private CodeDTView framework; //1
    private String point; //1

    public CodeDTView getFramework() {
        return framework;
    }

    public void setFramework(CodeDTView framework) {
        this.framework = framework;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
