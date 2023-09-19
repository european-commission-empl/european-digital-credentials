package eu.europa.ec.empl.edci.constants;

import eu.europa.ec.empl.edci.exception.EDCIException;
import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNDEFINED("UN-0000"),

    ENDPOINT_NOT_FOUND("GL-0001"),

    WALLET_INVALID_EMAIL("WL-0001"),
    WALLET_INVALID_ID("WL-0002"),
    WALLET_NOT_FOUND("WL-0003"),
    WALLET_EMAIL_RECIPIENT_INVALID("WL-0004"),
    WALLET_EMAIL_SEND_ERROR("WL-0005"),
    WALLET_EXISTS("WL-0006", "wallet.already.extists.mail.error", HttpStatus.BAD_REQUEST),
    WALLET_TEMPORARY("WL-0007"),

    CONVERSION_INTERNAL_ISSUE("CI-0001"),
    CONVERSION_EXTERNAL_ISSUE("CI-0002"),
    CONVERSION_JSON_FORMAT_ERROR("CI-0003"),
    CONVERSION_DIPLOMA_ERROR("CI-0004"),
    CONVERSION_XML_TO_JSON_ERROR("CI-0005"),
    CONVERSION_XML_SIGNATURE_INVALID("CI-0006"),
    CONVERSION_XML_SIGNATURE_EXTEND_ERROR("CI-0007"),
    CONVERSION_XML_SIGNATURE_NOT_FOUND("CI-0008"),
    CONVERSION_XML_FORMAT_ERROR("CI-0009"),
    CONVERSION_BAD_REQUEST("CI-0010"),

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
    CREDENTIAL_MALFORMED_SUBJECT("CR-0010", "credential.malformed.subject", HttpStatus.BAD_REQUEST),
    CREDENTIAL_NOT_FOUND("CR-0011", "credential.file.notFound", HttpStatus.BAD_REQUEST),

    PUBLIC_CREDENTIAL_CANNOT_CREATE("PCR-0001"),
    PUBLIC_CREDENTIAL_NO_CONTACT("PCR-0002"),

    DATABASE_ERROR("IE-0001"),

    FILE_BASE_DATA("FB-0001"),

    REFLECTION("RE-0001"),

    SESSION_EXPIRED("SE-0001", "exception.session.expired", HttpStatus.FORBIDDEN),

    FORBIDDEN("AU-0001"),

    UNAUTHORIZED("AU-0002"),

    LOCAL_CERTIFICATE_NOT_DEFINED("LC-0001"),
    LOCAL_CERTIFICATE_NOT_FOUND("LC-0002"),
    CANNOT_WRITE_SIGNED_CRED("LC-0003"),
    CANNOT_READ_LOCAL_CERTIFICATE("LC-0004"),
    LOCAL_CERTIFICATE_BAD_PASSWORD("LC-0005"),
    CANNOT_SEND_CONFIGURED_WALLET("LC-0006"),
    CANNOT_SIGN_ON_BEHALF("LC-0007"),

    ACCREDITATION_NOT_FOUND("ACC-0001"),

    DIPLOMA_BAD_FORMAT("DI-0001", "diploma.bad.format", HttpStatus.BAD_REQUEST),

    CUSTOMIZABLE_ENTITIES_NOT_FOUND("CU-0001", "customization.error.no.customizable.entities.found", HttpStatus.INTERNAL_SERVER_ERROR),
    CUSTOMIZABLE_CREDENTIAL_NOT_FOUND("CU-0002", "customization.error.customizable.credential.notFound", HttpStatus.NOT_FOUND),
    CUSTOMIZABLE_CLASS_NOT_FOUND("CU-0003", "customization.class.not.found", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_CLASS_NO_CUSTOMIZABLE_ENTITY("CU-0004", "customization.class.not.customizable.entity", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_CLASS_FIELD_NOT_FOUND("CU-0005", "customization.class.field.not.found", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_CLASS_RELATION_NOT_FOUND("CU-0006", "customization.class.relation.not.found", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_IDENTIFIER_FIELD_NOT_FOUND("CU-0007", "global.internal.error", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_BAD_MULTILINGUAL_FORMAT("CU-0008", "global.internal.error", HttpStatus.INTERNAL_SERVER_ERROR),
    CUSTOMIZABLE_SHOULD_INSTANCE_METHOD_NOT_FOUND("CU-0009", "global.internal.error", HttpStatus.INTERNAL_SERVER_ERROR),
    CUSTOMIZABLE_RECIPIENT_XLS_NOT_READABLE("CU-0010", "customization.recipient.xls.not.readable", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_TYPE_HEADER_NOT_FOUND("CU-0011", "customization.xls.field.type.header.not.found", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_RECIPIENT_NO_FIELDS("CU-0012", "customization.xls.recipient.no.fields", HttpStatus.NOT_FOUND),
    CUSTOMIZABLE_XLS_CREDENTIAL_ID_MISMATCH("CU-0013", "customization.xls.credential.id.mismatch", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_INVALID_CREDENTIAL_ID_FORMAT("CU-0014", "customization.xls.invalid.credential.id.format", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_INVALID_FIELD_FORMAT_FIELDPATH("CU-0015", "customization.xls.invalid.field.format.fieldpath", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_INSTANCE_ERROR("CU-0016", "customization.xls.field.instance.error", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_RELATION_NOT_FOUND("CU-0017", "customization.xls.relation.not.found", HttpStatus.NOT_FOUND),
    CUSTOMIZABLE_XLS_RELATION_ERROR_GET("CU-0018", "customization.xls.relation.error.get", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_RELATION_ERROR_ADD("CU-0019", "customization.xls.relation.error.add", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_CREATE_POSITION_ERROR("CU-0020", "customization.xls.field.create.position.erro", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_CREATE_POSITION_NOT_FOUND("CU-0021", "customization.xls.field.create.position.not.found", HttpStatus.NOT_FOUND),
    CUSTOMIZABLE_XLS_FIELD_NOT_LIST_POSITION("CU-0022", "customization.xls.field.not.list.position", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_NOT_FOUND("CU-0023", "customization.xls.field.not.found", HttpStatus.NOT_FOUND),
    CUSTOMIZABLE_XLS_FIELD_NOT_CL_FIELD("CU-0024", "customization.xls.field.not.cl.field", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_DATE_FORMAT_INVALID("CU-0025", "customization.xls.date.format.invalid", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_TYPE_NOT_SUPPORTED("CU-0026", "customization.xls.field.type.not.supported", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_INVALID_CL_CONCEPT("CU-0027", "customization.xls.field.invalid.cl.concept", HttpStatus.BAD_REQUEST),
    CUSTOMIZABLE_XLS_FIELD_NO_WALLET_EMAIL("CU-0028", "validation.contact.point.email.wallet.notnull", HttpStatus.BAD_REQUEST),

    JSONLD_INVALID_JSON_INPUT("JLD-0001", "jsonld.invalid.json.input", HttpStatus.BAD_REQUEST);


    String code;

    String labelKey;

    HttpStatus httpStatus;


    ErrorCode(String code) {
        if (code != null) {
            this.code = code;
        } else {
            throw new EDCIException().addDescription("Trying to create an empty ErrorCode");
        }
    }

    ErrorCode(String code, String labelKey, HttpStatus httpStatus) {
        this.code = code;
        this.labelKey = labelKey;
        this.httpStatus = httpStatus;
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
