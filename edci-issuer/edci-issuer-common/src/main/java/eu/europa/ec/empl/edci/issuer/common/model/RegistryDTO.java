package eu.europa.ec.empl.edci.issuer.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The type App dto.
 */
public class RegistryDTO implements Serializable {

	private Long regId;
	private Date regDate;

	public Long getRegId() {
		return regId;
	}

	public void setRegId(Long regId) {
		this.regId = regId;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
}
