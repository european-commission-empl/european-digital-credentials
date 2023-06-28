package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;


public class QualificationFieldView {

    private String EQFLevel; //0..1
    private List<String> NQFLevel; //*
    private Boolean isPartialQualification; //0..1
    private List<String> qualificationCode; //*
    private List<AccreditationFieldView> accreditation; //*


    public String getEQFLevel() {
        return EQFLevel;
    }

    public void setEQFLevel(String EQFLevel) {
        this.EQFLevel = EQFLevel;
    }

    public List<String> getNQFLevel() {
        return NQFLevel;
    }

    public void setNQFLevel(List<String> NQFLevel) {
        this.NQFLevel = NQFLevel;
    }

    public Boolean getPartialQualification() {
        return isPartialQualification;
    }

    public void setPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public Boolean getIsPartialQualification() {
        return isPartialQualification;
    }

    public void setIsPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public List<String> getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(List<String> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    public List<AccreditationFieldView> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(List<AccreditationFieldView> accreditation) {
        this.accreditation = accreditation;
    }
}