package eu.europa.ec.empl.edci.exception;

import eu.europa.ec.empl.edci.constants.ErrorCode;

public interface I18NException {

    public String getMessageKey();

    public String[] getMessageArgs();

    public ErrorCode getCode();

}
