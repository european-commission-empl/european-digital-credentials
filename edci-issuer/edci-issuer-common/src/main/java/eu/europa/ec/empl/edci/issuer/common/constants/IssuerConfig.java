package eu.europa.ec.empl.edci.issuer.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIConfig;

public class IssuerConfig extends EDCIConfig {

    public class Issuer {
        public static final String ACTIVE_PROFILE = "active.profile";
        public static final String MAIL_SMTP_USER = "mail.smtp.user";
        public static final String MAIL_SMTP_PASS = "mail.smtp.pass";
        public static final String MAIL_SMTP_HOST = "mail.smtp.host";
        public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
        public static final String MAIL_DEBUG = "mail.debug";
        public static final String MAIL_SMTP_STARTTLS = "mail.smtp.starttls.enable";
        public static final String LOAD_CONTROLLED_LISTS_STARTUP = "load.controlled.lists.on.startup";
        public static final String LOAD_CONTROLLED_LISTS_MONTHLY = "load.controlled.lists.monthly";
        public static final String TMP_DATA_LOCATION = "tmp.data.location";
        public static final String TMP_PUBLIC_CRED_FOLDER = "tmp.data.public.credential.folder";
        public static final String TMP_CRED_FOLDER = "tmp.data.credential.folder";
        public static final String UPLOAD_FILE_CREDENTIAL_REGEX = "upload.file.credential.regex";
        public static final String WALLET_API_URL = "wallet.url";
        public static final String WALLET_SEND_EMAIL = "wallet.send.email";
        public static final String WALLET_ADD_PATH = "wallet.credential.add.path";
        public static final String RDF_SPARQL_ENDPOINT = "publications.rdf.sparql.endpoint";
        public static final String ALLOW_QSEALS_ONLY = "eseal.allow.qseals.only";
        public static final String LOCAL_CERT_PATH = "local.cert.path";
        public static final String SEALING_API_SEND_TEMPORAL = "sealing.api.send.temporal";
        public static final String MAX_ERRORS_BATCH_SEALING = "max.errors.batch.sealing";
        public static final String MAX_CONSECUTIVE_ERRORS_BATCH_SEALING = "max.consecutive.errors.batch.sealing";
    }

    public class Path {
        public static final String SECURITY_FILE = "file:${edci.properties.base}/issuer/security.properties";
        public static final String ISSUER_FILE = "file:${edci.properties.base}/issuer/issuer.properties";
        public static final String PROXY_FILE = "file:${edci.properties.base}/proxy.properties";
        public static final String FRONT_FILE = "file:${edci.properties.base}/issuer/issuer_front.properties";
    }

    public static class Threads {
        public static final String SIGNATURE_BYTES_NUM_THREADS = "signatureBytes.num.threads";
        public static final String SIGNATURE_BYTES_TIMEOUT_MINUTES_THREADS = "signatureBytes.timeout.minutes.threads";
        public static final String SIGN_CREDENTIAL_NUM_THREADS = "signCredential.num.threads";
        public static final String SIGN_CREDENTIALS_TIMEOUT_MINUTES_THREADS = "signCredential.timeout.minutes.threads";
        public static final String SEND_CREDENTIALS_NUM_THREADS = "sendCredential.num.threads";
        public static final String SEND_CREDENTIAL_TIMEOUT_MINUTES_THREADS = "sendCredential.timeout.minutes.threads";
        public static final String LOCAL_BATCH_SIGN_NUM_THREADS = "local.batch.sign.num.threads";
        public static final String LOCAL_BATCH_SIGN_TIMEOUT_MINUTES_THREADS = "local.batch.sign.timeout.minutes.threads";
    }
}
