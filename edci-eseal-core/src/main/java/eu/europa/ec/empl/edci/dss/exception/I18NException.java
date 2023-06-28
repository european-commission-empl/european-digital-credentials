package eu.europa.ec.empl.edci.dss.exception;

import eu.europa.ec.empl.edci.dss.constants.ErrorCode;

public interface I18NException {

    public String getMessageKey();

    public String[] getMessageArgs();

    public ErrorCode getCode();

}
