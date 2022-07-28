package eu.europa.ec.empl.edci.wallet.common.model.verification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class QMSAccreditation {
    private Date issuedDate;
    private Date expiryDate;
    private List<LimitField> limitField = new ArrayList<LimitField>();
    private List<LimitEQFLevel> limitEQFLevel = new ArrayList<LimitEQFLevel>();
    private LimitJurisdiction limitJurisdiction;

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<LimitField> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<LimitField> limitField) {
        this.limitField = limitField;
    }

    public List<LimitEQFLevel> getLimitEQFLevel() {
        return limitEQFLevel;
    }

    public void setLimitEQFLevel(List<LimitEQFLevel> limitEQFLevel) {
        this.limitEQFLevel = limitEQFLevel;
    }

    public LimitJurisdiction getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(LimitJurisdiction limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;

    }
}
