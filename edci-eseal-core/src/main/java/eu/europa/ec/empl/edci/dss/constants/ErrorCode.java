package eu.europa.ec.empl.edci.dss.constants;

import eu.europa.ec.empl.edci.dss.exception.ESealException;
import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNDEFINED("UN-0000"),
    LOCAL_CERTIFICATE_NOT_DEFINED("EC-0001"),
    LOCAL_CERTIFICATE_NOT_FOUND("EC-0002"),
    CANNOT_READ_LOCAL_CERTIFICATE("EC-0003"),
    LOCAL_CERTIFICATE_BAD_PASSWORD("EC-0004");

    String code;

    String labelKey;

    HttpStatus httpStatus;


    ErrorCode(String code) {
        if (code != null) {
            this.code = code;
        } else {
            throw new ESealException().addDescription("Trying to create an empty ErrorCode");
        }
    }

    public static ErrorCode fromCode(String code) {
        for (ErrorCode b : ErrorCode.values()) {
            if (b.code.equalsIgnoreCase(code)) {
                return b;
            }
        }
        return UNDEFINED;
    }

    public String getCode() {
        return code;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public boolean equals(ErrorCode code) {
        if (code == null) {
            return false;
        } else {
            return code.getCode().equals(code);
        }
    }

}
