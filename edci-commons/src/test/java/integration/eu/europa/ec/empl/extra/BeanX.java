package integration.eu.europa.ec.empl.extra;

import javax.validation.constraints.NotNull;

public class BeanX {

    @NotNull(message = "Default is null")
    private String groupDefaultField; //1

    @NotNull(message = "A is null", groups = GroupA.class)
    private String groupAField; //1

    @NotNull(message = "B is null", groups = GroupB.class)
    private String groupBField; //1

    @NotNull(message = "AB is null", groups = {GroupA.class, GroupB.class})
    private String groupABField; //1

    public BeanX() {
    }

    public BeanX(String groupAField, String groupBField, String groupABField, String groupDefaultField) {
        this.groupAField = groupAField;
        this.groupBField = groupBField;
        this.groupABField = groupABField;
        this.groupDefaultField = groupDefaultField;

    }

    public String getGroupAField() {
        return groupAField;
    }

    public void setGroupAField(String groupAField) {
        this.groupAField = groupAField;
    }

    public String getGroupBField() {
        return groupBField;
    }

    public void setGroupBField(String groupBField) {
        this.groupBField = groupBField;
    }

    public String getGroupABField() {
        return groupABField;
    }

    public void setGroupABField(String groupABField) {
        this.groupABField = groupABField;
    }

    public String getGroupDefaultField() {
        return groupDefaultField;
    }

    public void setGroupDefaultField(String groupDefaultField) {
        this.groupDefaultField = groupDefaultField;
    }
}