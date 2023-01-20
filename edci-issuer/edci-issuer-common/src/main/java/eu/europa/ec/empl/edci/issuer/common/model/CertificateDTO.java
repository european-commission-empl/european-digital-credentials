package eu.europa.ec.empl.edci.issuer.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The type App dto.
 */
public class CertificateDTO implements Serializable {

	private Long id;
	private Date date;
	private String xml;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
