package eu.europa.ec.empl.edci.constants;

import eu.europa.ec.empl.edci.exception.EDCIException;

public enum ErrorCode {

    UNDEFINED("UN-0000"),

    ENDPOINT_NOT_FOUND("GL-0001"),

    WALLET_INVALID_EMAIL("WL-0001"),
    WALLET_INVALID_ID("WL-0002"),
    WALLET_NOT_FOUND("WL-0003"),
    WALLET_EMAIL_RECIPIENT_INVALID("WL-0004"),
    WALLET_EMAIL_SEND_ERROR("WL-0005"),
    WALLET_EXISTS("WL-0006"),
    WALLET_TEMPORARY("WL-0007"),

    SHARE_LINK_EXPIRED("SL-0001"),
    SHARE_LINK_INVALID("SL-0002"),

    CREDENTIAL_EXISTS_UUID("CR-0001"),
    CREDENTIAL_NOT_EXISTS_ID("CR-0002"),
    CREDENTIAL_NOT_EXISTS_UUID("CR-0003"),
    CREDENTIAL_NOT_EXISTS_VARIOUS("CR-0004"),
    CREDENTIAL_INVALID_FORMAT("CR-0005"),
    CREDENTIAL_NOT_READABLE("CR-0006"),
    CREDENTIAL_UNSIGNED("CR-0007"),
    CREDENTIAL_SCHEMA_LOCATION_ERR("CR-0008"),
    CREDENTIAL_ALREADY_SIGNED("CR-0009"),

    PUBLIC_CREDENTIAL_CANNOT_CREATE("PCR-0001"),
    PUBLIC_CREDENTIAL_NO_CONTACT("PCR-0002"),

    DATABASE_ERROR("IE-0001"),

    FILE_BASE_DATA("FB-0001"),

    REFLECTION("RE-0001"),

    SESSION_EXPIRED("SE-0001"),

    FORBIDDEN("AU-0001"),

    UNAUTHORIZED("AU-0002"),

    LOCAL_CERTIFICATE_NOT_DEFINED("LC-0001"),
    LOCAL_CERTIFICATE_NOT_FOUND("LC-0002"),
    CANNOT_WRITE_SIGNED_CRED("LC-0003"),
    CANNOT_READ_LOCAL_CERTIFICATE("LC-0004"),
    LOCAL_CERTIFICATE_BAD_PASSWORD("LC-0005"),
    CANNOT_SEND_CONFIGURED_WALLET("LC-0006"),
    CANNOT_SIGN_ON_BEHALF("LC-0007");

    String code;

    ErrorCode(String code) {
        if (code != null) {
            this.code = code;
        } else {
            throw new EDCIException().addDescription("Trying to create an empty ErrorCode");
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
