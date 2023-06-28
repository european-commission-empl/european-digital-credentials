package eu.europa.ec.empl.edci.issuer.common.constants;

public abstract class EDCIIssuerMessageKeys {

    public static class Sealing {
        public static final String SEAL_CREDENTIAL_KO = "seal-credential.message.seal-ko";
        public static final String CREDENTIAL_ALREADY_SIGNED = "upload.credential.already.signed";
        public static final String CREDENTIALS_ALREADY_SIGNED = "upload.credentials.already.signed";

    }

    public static class Exception {
        public static final String PUBLIC_CRED_CANNOT_CREATE = "public.cred.cannot.create";
        public static final String CANNOT_SEND_CONFIGURED_WALLET = "cannot.send.configured.wallet";
    }

    public static class Customization {
        public static final String INCLUDE_ELEMENT_DESCRIPTION = "customization.include.element.description";
        public static final String DESCRIPTION_MANDATORY = "customization.description.mandatory";
        public static final String DESCRIPTION_OPTIONAL = "customization.description.optional";
    }

    public static class JSONLD {
        public static final String INVALID_TYPE = "jsonld.invalid.type";
        public static final String INVALID_PROFILE = "jsonld.invalid.profile";
    }

    public static final String PUBLIC_CRED_NO_CONTACT = "public.cred.no.contact";
    public static final String FILE_TEMP_NOT_FOUND = "error.file.temp.cred.not.found";

    public static final String CREDENTIAL_FILE_NOT_FOUND = "credential.file.no.found";
    public static final String GLOBAL_INTERNAL_ERROR = "global.internal.error";

    public static final String ERROR_NO_EMAIL_FOUND_XLS = "email.not.found.xls.error";
    public static final String ERROR_INVALID_EMAIL = "email.invalid.email.error";
    public static final String ERROR_INVALID_EMAIL_SEND_WALLET = "email.invalid.email.send.wallet.error";
    public static final String ERROR_NO_WALLET_FOUND_XLS = "wallet.address.not.found.xls.error";
    public static final String ERROR_SEND_EMAIL = "email.send.error";
    public static final String ERROR_SEND_WALLET = "wallet.send.error";
    public static final String ERROR_DOWNLOADABLE_ASSET = "downloadable.asset.error";
    public static final String ERROR_UNRELATED_ASSESSMENT = "error.unrelated.assessment";
    public static final String ERROR_RECIPIENT_TEMPLATE_INVALID = "error.recipient.template.invalid";

    public static final String MAIL_SUBJECT_YOUR = "mail.subject.your";

    public static final String REQUIRED_CL_ITEM_NOTFOUND = "required.cl.item.notfound";
    public static final String REQUIRED_ACC_ITEM_NOTFOUND = "required.acc.item.notfound";
    public static final String REQUIRED_ID_ACC_DOWNLOAD = "required.id.acc.download";


    public static final String HEADER_GIVENNAME = "header.givenName";
    public static final String HEADER_FAMILYNAME = "header.familyName";
    public static final String HEADER_DATEOFBIRTH = "header.dateOfBirth";
    public static final String HEADER_DATEOFBIRTH_DESCRIPTION = "header.dateOfBirth.description";
    public static final String HEADER_GENDER = "header.gender";
    public static final String HEADER_NATIONALID = "header.nationalId";
    public static final String HEADER_NATIONALID_NUMBER_DESCRIPTION = "header.nationalId.number.description";
    public static final String HEADER_NATIONALID_COUNTRY_DESCRIPTION = "header.nationalId.country.description";
    public static final String HEADER_PLACEOFBIRTH = "header.placeOfBirth";
    public static final String HEADER_PLACEOFBIRTH_DESCRIPTION = "header.placeOfBirth.description";
    public static final String HEADER_COUNTRYOFCITIZENSHIP = "header.countryOfCitizenship";
    public static final String HEADER_COUNTRYOFCITIZENSHIP_DESCRIPTION = "header.countryOfCitizenship.description";
    public static final String HEADER_IDENTIFIER1_SCHEMENAME = "header.identifier1SchemeName";
    public static final String HEADER_IDENTIFIER1_SCHEMENAME_DESCRIPTION = "header.identifier1SchemeName.description";
    public static final String HEADER_IDENTIFIER1 = "header.identifier1";
    public static final String HEADER_IDENTIFIER1_DESCRIPTION = "header.identifier1.description";
    public static final String HEADER_IDENTIFIER2_SCHEMENAME = "header.identifier2SchemeName";
    public static final String HEADER_IDENTIFIER2_SCHEMENAME_DESCRIPTION = "header.identifier2SchemeName.description";
    public static final String HEADER_IDENTIFIER2 = "header.Identifier2";
    public static final String HEADER_IDENTIFIER2_DESCRIPTION = "header.Identifier2.description";
    public static final String HEADER_EMAILADDRESS = "header.emailAddress";
    public static final String HEADER_WALLETADDRESS = "header.walletAddress";
    public static final String HEADER_FULLADDRESS = "header.fullAddress";
    public static final String HEADER_COUNTRYOFRESIDENCE = "header.countryOfResidence";
    public static final String HEADER_COUNTRYOFRESIDENCE_DESCRIPTION = "header.countryOfResidence.description";
    public static final String HEADER_LABEL = "header.label";
    public static final String HEADER_DEFINITION = "header.definition";
    public static final String HEADER_LANGUAGE = "header.language";
    public static final String HEADER_DEFAULT = "header.default";
}
