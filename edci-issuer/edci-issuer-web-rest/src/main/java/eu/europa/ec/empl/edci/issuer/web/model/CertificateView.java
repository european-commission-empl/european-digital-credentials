package eu.europa.ec.empl.edci.issuer.web.model;

import java.util.Date;

/**
 * The type User view.
 */
public class CertificateView {

    private Long id;
    private Date date;
    private String xml;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
