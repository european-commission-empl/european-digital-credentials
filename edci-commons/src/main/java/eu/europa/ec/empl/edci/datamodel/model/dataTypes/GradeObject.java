package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

public class GradeObject {
    private String origin;
    private int originRef;
    private int gradedRef;
    private Object grade;

    public GradeObject(String origin, int originRef, int gradedRef, Object grade) {
        this.setOrigin(origin);
        this.setOriginRef(originRef);
        this.setGradedRef(gradedRef);
        this.setGrade(grade);
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getOriginRef() {
        return originRef;
    }

    public void setOriginRef(int originRef) {
        this.originRef = originRef;
    }

    public int getGradedRef() {
        return gradedRef;
    }

    public void setGradedRef(int gradedRef) {
        this.gradedRef = gradedRef;
    }

    public Object getGrade() {
        return grade;
    }

    public void setGrade(Object grade) {
        this.grade = grade;
    }
}
