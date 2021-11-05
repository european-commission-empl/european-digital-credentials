package eu.europa.ec.empl.edci.issuer.web.model;

public class RegistryResponseView {
    private String message;
    private String code;
    private int records;

    public RegistryResponseView(){

    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }


}
