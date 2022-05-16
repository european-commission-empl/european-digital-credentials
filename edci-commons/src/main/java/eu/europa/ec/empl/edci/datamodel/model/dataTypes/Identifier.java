package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateTimeAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"content", "identifierSchemeId", "identifierSchemeName", "identifierSchemeAgencyName", "issuedDate", "identifierType"})
public class Identifier implements Nameable {

    @XmlValue
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_IDENTIFIER_CONTENT_NOTNULL)
    private String content; //1
    @XmlAttribute(name = "schemeID")
    private String identifierSchemeId; //0..1
    @XmlAttribute(name = "schemeName")
    private String identifierSchemeName; //0..1
    @XmlAttribute(name = "schemeAgencyName")
    private String identifierSchemeAgencyName; //0..1
    @XmlAttribute
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date issuedDate; //0..1
    @XmlAttribute(name = "type")
    private List<String> identifierType; //*

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "content", "identifierSchemeName", "identifierSchemeId", "identifierSchemeAgencyName");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentifierSchemeId() {
        return identifierSchemeId;
    }

    public void setIdentifierSchemeId(String identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }

    public String getIdentifierSchemeName() {
        return identifierSchemeName;
    }

    public void setIdentifierSchemeName(String identifierSchemeName) {
        this.identifierSchemeName = identifierSchemeName;
    }

    public String getIdentifierSchemeAgencyName() {
        return identifierSchemeAgencyName;
    }

    public void setIdentifierSchemeAgencyName(String identifierSchemeAgencyName) {
        this.identifierSchemeAgencyName = identifierSchemeAgencyName;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public List<String> getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(List<String> identifierType) {
        this.identifierType = identifierType;
    }

    //XML Getters
    public String getSchemeID() {
        return this.identifierSchemeName;
    }

    public String getSchemeName() {
        return this.identifierSchemeName;
    }

    public String getSchemeAgencyName() {
        return this.identifierSchemeAgencyName;
    }

    public List<String> getType() {
        return this.identifierType;
    }

    public List<String> getTypes() {
        return this.identifierType;
    }

    @Override
    public String toString() {
        return this.content;
    }
}