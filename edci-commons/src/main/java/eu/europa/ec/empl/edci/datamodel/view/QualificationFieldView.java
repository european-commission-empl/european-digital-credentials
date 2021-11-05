package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;


public class QualificationFieldView {

    private String eqfLevel; //0..1
    private List<String> nqfLevel; //*
    private Boolean isPartialQualification; //0..1
    private List<String> qualificationCode; //*

    public String getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(String eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<String> getNqfLevel() {
        return nqfLevel;
    }

    public void setNqfLevel(List<String> nqfLevel) {
        this.nqfLevel = nqfLevel;
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
}