package eu.europa.ec.empl.edci.issuer.web.model;

import java.util.Date;

/**
 * The type User view.
 */
public class RegistryView {

	private long regId;
	private Date regDate;

	public long getRegId() {
		return regId;
	}

	public void setRegId(long regId) {
		this.regId = regId;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
}
