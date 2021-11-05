package eu.europa.ec.empl.edci.viewer.web.model;

public class DummyView {
    private String studentName;
    private String course;
    private String uuid;
    private Boolean valid;
    private Boolean sealed;
    private Boolean sent;
    private Boolean received;
    private String toBeSigned;

    public DummyView(){

    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Boolean isSealed() {
        return sealed;
    }

    public void setSealed(Boolean sealed) {
        this.sealed = sealed;
    }

    public Boolean isSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public Boolean isRecieved() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToBeSigned() {
        return toBeSigned;
    }

    public void setToBeSigned(String toBeSigned) {
        this.toBeSigned = toBeSigned;
    }
}
