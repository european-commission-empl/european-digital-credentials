package eu.europa.ec.empl.edci.constants;

public class EDCIMessageKeys {

    public class Exception {
        public class XLS {
            public static final String FILE_EXCEL_CELL_ERROR_MESSAGE = "exception.file.excel.cell.error.message";
            public static final String FILE_EXCEL_CREDENTIAL_NOTFOUND = "exception.file.excel.credentials.notfound";
            public static final String FILE_EXCEL_CREDENTIAL_GENERATION = "exception.file.excel.credentials.generation";
            public static final String FILE_EXCEL_HOLDERCLASS_NOTVALID = "exception.file.excel.holderclass.notvalid";
            public static final String FILE_EXCEL_HOLDERFIELD_NOTVALID = "exception.file.excel.holderfield.notvalid";
            public static final String FILE_EXCEL_ENTITY_NOTPARTOFDATAMODEL = "exception.file.excel.entity.notpartofdatamodel";
            public static final String FILE_EXCEL_NESTEDASSOCIATION_NOTFOUND = "exception.file.excel.nestedassociation.notfound";
            public static final String FILE_EXCEL_NESTEDPROPERTY_NOTFOUND = "exception.file.excel.nestedproperty.notfound";
            public static final String FILE_EXCEL_EXTERNALASSOCIATION_INVALIDFORMAT = "exception.file.excel.externalassociation.invalidformat";
            public static final String FILE_EXCEL_ENTITY_NOTFOUND = "exception.file.excel.entity.notfound";
            public static final String FILE_EXCEL_ASSOCIATION_ERROR = "exception.file.excel.association.error";
            public static final String FILE_EXCEL_NESTEDPROPERTY_MISSINGINFO = "exception.file.excel.nestedassociation.missinginfo";
            public static final String FILE_EXCEL_CAST_FAILED = "exception.file.excel.cast.failed";
            public static final String FILE_EXCEL_ASSOCIATION_PROCESS_ERROR = "exception.file.excel.association.process.error";
            public static final String FILE_EXCEL_PARAMETERPATH_NOTVALID = "exception.file.excel.parameterpath.notvalid";
            public static final String FILE_EXCEL_PARAMETER_NOTALIST = "exception.file.excel.parameter.notalist";
            public static final String FILE_EXCEL_PARAMETER_ISLIST = "exception.file.excel.parameter.islist";
            public static final String FILE_EXCEL_ASSOCIATION_FORMAT_ERORR = "exception.file.excel.association.format.error";
            public static final String FILE_EXCEL_FORMAT_SHEET_ERROR = "exception.file.excel.format.sheet.error";
            public static final String FILE_EXCEL_DEFINITION_FORMAT = "exception.file.excel.definition.format";
            public static final String FILE_EXCEL_NONSTRING_LIST_ITEM = "exception.file.excel.nonstring.list.item";
            public static final String FILE_EXCEL_DATE_FORMAT_ERROR = "exception.file.excel.date.format.error";
            public static final String FILE_EXCEL_UNVALID_COLUMINFO = "exception.file.excel.unvalid.columninfo";
            public static final String FILE_EXCEL_ENTITY_LIST = "exception.file.excel.entity.list";
            public static final String FILE_EXCEL_EMPTY = "exception.file.excel.empty";
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

    public class Validation {
        public static final String VALIDATION_XSD_NOTMATCH = "validation.xsd.notmatch";
        public static final String VALIDATION_CREDENTIAL_ID_NOTNULL = "validation.credential.id.notnull";
        public static final String VALIDATION_CREDENTIAL_ISSUED_NOTNULL = "validation.credential.issued.notnull";
        public static final String VALIDATION_CREDENTIAL_ISSUANCEDATE_NOTNULL = "validation.credential.issuanceDate.notnull";
        public static final String VALIDATION_CREDENTIAL_TYPE_NOTNULL = "validation.credential.type.notnull";
        public static final String VALIDATION_CREDENTIAL_TITLE_NOTNULL = "validation.credential.title.notnull";
        public static final String VALIDATION_CREDENTIAL_CREDENTIALSUBJECT_NOTNULL = "validation.credential.credentialSubject.notnull";
        public static final String VALIDATION_CREDENTIAL_ISSUER_NOTNULl = "validation.credential.issuer.notnull";

        public static final String VALIDATION_VP_ID_NOTNULL = "validation.vp.id.notnull";

        public static final String VALIDATION_IDENTIFIER_CONTENT_NOTNULL = "validation.identifier.content.notnull";

        public static final String VALIDATION_CODE_URI_NOTNULL = "validation.code.uri.notnull";
        public static final String VALIDATION_CODE_TARGETNAME_NOTNULL = "validation.code.targetname.notnull";

        public static final String VALIDATION_NOTE_CONTENT_MIN = "validation.note.content.min";

        public static final String VALIDATION_TEXT_CONTENT_NOTNULL = "validation.text.content.notnull";

        public static final String VALIDATION_MEDIAOBJECT_CONTENTTYPE_NOTNULL = "validation.mediaObject.contentType.notnull";
        public static final String VALIDATION_MEDIAOBJECT_CONTENTENCODING_NOTNULL = "validation.mediaObject.contentEncoding.notnull";

        public static final String VALIDATION_LEGALIDENTIFIER_SPATIALID_NOTNULL = "validation.legalidentifier.spatialid.notnull";

        public static final String VALIDATION_AGENT_ID_NOTNULL = "validation.agent.id.notnull";

        public static final String VALIDATION_CONTACT_POINT = "validation.contact.point.email.wallet.notnull";

        public static final String VALIDATION_PERSON_GIVENNAMES_NOTNULL = "validation.person.givenNames.notnull";
        public static final String VALIDATION_PERSON_FAMILYNAME_NOTNULL = "validation.person.familyName.notnull";
        public static final String VALIDATION_PERSON_DATEOFBIRTH_NOTNULL = "validation.person.dateOfBirth.notnull";

        public static final String VALIDATION_MAILBOX_ID_NOTNULL = "validation.mailbox.id.notnull";
        public static final String VALIDATION_MAILBOX_ID_EMAILFORMAT = "validation.mailbox.id.emailformat";

        public static final String VALIDATION_ORGANIZATION_LEGALIDENTIFIER_NOTNULL = "validation.organization.legalIdentifier.notnull";
        public static final String VALIDATION_ORGANIZATION_PREFERREDNAME_NOTNULL = "validation.organization.preferredname.notnull";
        public static final String VALIDATION_ORGANIZATION_LOCATION_MIN = "validation.organization.location.min";

        public static final String VALIDATION_QUALIFICATIONAWARD_SPECIFIEDBY_NOTNULL = "validation.qualificationaward.specifiedby.notnull";

        public static final String VALIDATION_ACCREDITATION_ACCREDITATIONTYPE_NOTNULL = "validation.accreditation.accreditationType.notnull";
        public static final String VALIDATION_ACCREDITAION_ORGANISATION_NOTNULL = "validation.accreditation.organisation.notnull";

        public static final String VALIDATION_LEARNINGACTIVITY_ID_NOTNULL = "validation.learningactivity.id.notnull";
        public static final String VALIDATION_LEARNINGACTIVITY_LEARNINGACTSPEC_NOTNULL = "validation.learningactivity.learningspec.notnull";
        public static final String VALIDATION_LEARNINGACTIVITY_TITLE_NOTNULL = "validation.learningactivity.title.notnull";

        public static final String VALIDATION_LEARNINGACTIVITYSPEC_ID_NOTNULL = "validation.learningactspec.id.notnull";

        public static final String VALIDATION_LEARNINGSPEC_ID_NOTNULL = "validation.learningspec.id.notnull";
        public static final String VALIDATION_LEARNINGSPEC_TITLE_NOTNULL = "validation.learningspec.title.notnull";

        public static final String VALIDATION_AMOUNT_CONTENT_NOTNULL = "validation.amount.content.notnull";
        public static final String VALIDATION_AMOUNT_UNIT_NONTNULL = "validation.amount.id.notnull";

        public static final String VALIDATION_MEASURE_CONTENT_NOTNULL = "validation.measure.content.notnull";
        public static final String VALIDATION_MEASURE_UNIT_NOTNULL = "validation.measure.unit.notnull";

        public static final String VALIDATION_MEDIAOBJECT_CODE_NOTNULL = "validation.mediaobject.code.notnull";
        public static final String VALIDATION_MEDIAOBJECT_CONTENT_NOTNULL = "validation.mediaobject.content.notnull";

        public static final String VALIDATION_NOTATION_CONTENT_NOTNULL = "validation.notation.content.notnull";

        public static final String VALIDATION_SCORE_CONTENT_NOTNULL = "validation.score.content.notnull";

        public static final String VALIDATION_ADDRESS_COUNTRYCODE_NOTNULL = "validation.address.countrycode.notnull";

        public static final String VALIDATION_ASSESSMENT_ID_NOTNULL = "validation.assesment.id.notnull";
        public static final String VALIDATION_ASSESSMENT_TITLE_NOTNULL = "validation.assessment.title.notnull";
        public static final String VALIDATION_ASSESSMENT_GRADE_NOTNULL = "validation.assessment.grade.notnull";

        public static final String VALIDATION_ASSESSMENTSPEC_ID_NOTNULL = "validation.assessmentspec.id.notnull";

        public static final String VALIDATION_AWARDINGOPPORTUNITY_ID_NOTNULL = "validation.awardingopportunity.id.notnull";
        public static final String VALIDATION_AWARDINGOPPORTUNITY_LEARNINGSPEC_NOTNULL = "validation.awardingopportunity.learningspec.notnull";

        public static final String VALIDATION_AWARDINGPROCESS_ID_NOTNULL = "validation.awardingprocess.id.notnull";
        public static final String VALIDATION_AWARDINGPROCESS_AWARDINGBODY_NOTNULL = "validation.awardingprocess.awardingbody.notnull";

        public static final String VALIDATION_ENTITLEMENT_ID_NOTNULL = "validation.entitlement.id.notnull";
        public static final String VALIDATION_ENTITLEMENT_TITLE_NOTNULL = "validation.entitlement.title.notnull";

        public static final String VALIDATION_ENTITLEMENTSPEC_ID_NOTNULL = "validation.entitlementspec.id.notnull";
        public static final String VALIDATION_ENTITLEMENTSPEC_TYPE_NOTNULL = "validation.entitlementspec.title.notnull";
        public static final String VALIDATION_ENTITLEMENTSPEC_STATUS_NOTNULL = "validation.entitlementspec.status.notnull";


        public static final String VALIDATION_LEARNINGACHIEVEMENT_ID_NOTNULL = "validation.learningachievement.id.notnull";
        public static final String VALIDATION_LEARNINGACHIEVEMENT_TITLE_NOTNULL = "validation.learningachievement.title.notnull";
        public static final String VALIDATION_LEARNINGACHIEVEMENT_SPECIFIEDBY_NOTNULL = "validation.learningachievement.specifiedby.notnull";

        public static final String VALIDATION_LEARNINGOPPORTUNITY_ID_NOTNULL = "validation.learningopportunity.id.notnull";
        public static final String VALIDATION_LEARNINGOPPORTUNITY_TITLE_NOTNULL = "validation.learningopportunity.title.notnull";
        public static final String VALIDATION_LEARNINGOPPORTUNITY_SPECIFIEDBY_NOTNULL = "validation.learningopportunity.specifiedby.notnull";

        public static final String VALIDATION_LEARNINGOUTCOME_ID_NOTNULL = "validation.learningoutcome.id.notnull";
        public static final String VALIDATION_LEARNINGOUTCOME_NAME_NOTNULL = "validation.learningoutcome.name.notnull";


        public static final String VALIDATION_RESULTCATEGORY_LABEL_NOTNULL = "validation.resultcategory.label.notnull";
        public static final String VALIDATION_RESULTCATEGORY_COUNT_NOTNULL = "validation.resultcategory.count.notnull";

        public static final String VALIDATION_SCORINGSCHEME_ID_NOTNULL = "validation.scoringscheme.id.notnull";

        public static final String VALIDATION_SHORTENEDGRADING_LOWER_NOTNULL = "validation.shortenedgrading.lower.notnull";
        public static final String VALIDATION_SHORTENEDGRADING_EQUAL_NOTNULL = "validation.shortenedgrading.equal.notnull";
        public static final String VALIDATION_SHORTENEDGRADING_HIGHER_NOTNULL = "validation.shortenedgrading.higher.notnull";

        public static final String VALIDATION_WEBDOCUMENT_ID_NOTNULL = "validation.webdocument.id.notnull";

        public static final String VALIDATION_MESSAGE_VARIABLE_FIELDNAME = "{fieldName}";
        public static final String VALIDATION_MESSAGE_VARIABLE_INFORMATIVENAME_CLASSNAME = "{informativeName.className}";
        public static final String VALIDATION_MESSAGE_VARIABLE_INFORMATIVENAME_FIELDNAME = "{informativeName.fieldName}";
        public static final String VALIDATION_MESSAGE_VARIABLE_INFORMATIVENAME_FIELDVALUE = "{informativeName.fieldValue}";

        public static final String VALIDATION_MESSAGE_VARIABLE_UNFOUND = "validation.message.variable.unfound";

        //Add to file
        public static final String VALIDATION_ASSOCIATIONOBJECT_ISASSOCIATIONFOR_NOTNULL = "validation.association.isassociationfor.notnull";

    }

    public class Acreditation {
        public static final String INVALID_ISSUANCE_DATE = "invalid.issuance.date";
        public static final String INVALID_ACHIEVEMENT_ACCREDITATION_SPECIFIED = "invalid.achievement.accreditation.specified";
        public static final String INVALID_ORGANIZATIONAL_ACCREDITATION_SPECIFIED = "invalid.organizational.accreditation.specified";
    }
}
