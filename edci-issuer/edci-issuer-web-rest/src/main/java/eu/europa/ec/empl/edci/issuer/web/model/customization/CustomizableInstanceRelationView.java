package eu.europa.ec.empl.edci.issuer.web.model.customization;

import org.apache.commons.lang3.RandomStringUtils;

public class CustomizableInstanceRelationView {

    private String frontId;
    private Integer position;
    private Integer order;
    private String relPath;
    private String label;
    private Integer groupId;
    private String groupLabel;

    public CustomizableInstanceRelationView() {
        this.setFrontId(RandomStringUtils.randomAlphanumeric(10));
    }

    public String getFrontId() {
        return frontId;
    }

    public void setFrontId(String frontId) {
        this.frontId = frontId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getRelPath() {
        return relPath;
    }

    public void setRelPath(String relPath) {
        this.relPath = relPath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
