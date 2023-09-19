package eu.europa.ec.empl.edci.constants;

public class EDCIMessageKeys {

    public static final String LINE_MSG = "line.msg";
    public static final String COLUMN_MSG = "column.msg";

    public class FieldLabel {
        public static final String MAIN_ADDITIONAL_NOTE_TOPIC = "fieldLabel.moreInformation";
    }

    public class Exception {
        public class XLS {
            public static final String FILE_EXCEL_DATE_FORMAT_ERROR = "exception.file.excel.date.format.error";
            public static final String FILE_EXCEL_CELL_ERROR_MESSAGE = "exception.file.excel.cell.error.message";
        }

        public class Reflection {
            public static final String EXCEPTION_REFLECTION_NOSETTER = "exception.reflection.nosetter";
            public static final String EXCEPTION_REFLECTION_NOTASUBCLASS = "exception.reflection.notasubclass";
            public static final String EXCEPTION_REFLECTION_GETFIELD = "exception.reflection.getfield";
            public static final String EXCEPTION_REFLECTION_FIELDNOTFOUND = "exception.reflection.fieldnotfound";
            public static final String EXCEPTION_REFLECTION_INVALID_PROPERTYPATH = "exception.reflection.invalid.propertypath";
            public static final String EXCEPTION_REFLECTION_PROPERTYPATH_NOTALIST = "exception.reflection.propertypath.notalist";
        }

        public class DSS {
            public static final String INVALID_SIGNATURE_LEVEL = "exception.dss.invalid.signature.level";
            public static final String LOCAL_CERTIFICATE_NOT_DEFINED = "exception.local.certificate.not.defined";
            public static final String LOCAL_CERTIFICATE_NOT_FOUND = "local.cert.not.found";
            public static final String LOCAL_CERTIFICATE_BAD_PASSWORD = "local.cert.bad.password";
            public static final String CANNOT_READ_LOCAL_CERTIFICATE = "cannot.read.local.cert";
            public static final String CANNOT_WRITE_SIGNED_CRED = "cannot.write.signed.cred";
            public static final String CANNOT_SIGN_ON_BEHALF = "cannot.sign.on.behalf";
            public static final String CERTIFICATE_NOT_QSEAL_ERROR = "certificate.not.qseal.error";
        }

        public class Global {
            public static final String GLOBAL_INTERNAL_ERROR = "global.internal.error";
            public static final String GLOBAL_ERROR_CREATING_FILE = "global.error.creating.file";
            public static final String GLOBAL_LINE = "global.line";
            public static final String GLOBAL_COLUMN = "global.column";
        }

        public class Template {
            public static final String TEMPLATE_NOTFOUND = "template.notfound";
        }

        public class BadRquest {
            public static final String UPLOAD_CREDENTIAL_BAD_FORMAT = "upload.credential.bad.format";
            public static final String UPLOAD_CREDENTIALS_BAD_FORMAT = "upload.credentials.bad.format";
            public static final String UPLOAD_CREDENTIAL_NOT_READABLE = "upload.credential.not.readable";
            public static final String UPLOAD_INVALID_PROFILE = "upload.invalid.profile";
            public static final String CONTENT_TYPE_NOTFOUND = "exception.content.type.notfound";
            public static final String MISSING_PARAMETER = "error.missing.parameter";
            public static final String MISSING_REQUEST_PART = "error.missing.request.part";
        }


    }

    public class Acreditation {
        public static final String QDR_ACCREDITATION_NOT_FOUND = "qdr.accreditation.not.found";
        public static final String LIMIT_JURISDICTION_NOT_COVER_AWARDEDBY = "limit.jurisdiction.not.cover.awardedby";
        public static final String CREDENTIAL_NOT_ACCREDITED_CREDENTIAL = "credential.not.accredited.cred";
        public static final String EVIDENCE_ACCREDITATION_NOT_FOUND = "evidence.not.found";
        public static final String NO_TOP_LEVEL_ACHIEVEMENT = "no.top.level.achievement";
        public static final String CREDENTIAL_ISSUANCE_DATE_NOT_COVERED = "credential.issuance.date.not.covered";
        public static final String ACCREDITING_ORG_NO_COVER_AWARDING_BODY = "accrediting.org.no.cover.awarding.body";
        public static final String LIMIT_QUALIFICATION_NOT_COVER_LEARNING_SPECIFICATION = "limit.qualification.not.cover.learning.specification";
        public static final String LIMIT_FIELD_NOT_COVER_THEMATIC_AREA = "limit.field.not.cover.thematic.area";
        public static final String EQF_LEVEL_NOT_COVERED = "eqf.level.not.covered";
    }
}
