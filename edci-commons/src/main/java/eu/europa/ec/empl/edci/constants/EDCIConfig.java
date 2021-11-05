package eu.europa.ec.empl.edci.constants;

public abstract class EDCIConfig {

    public static final String APP_CONTEXT_ROOT = "app.context.root";
    public static final String ALLOWED_DOMAINS = "allowed.domains";

    public static final String SCHEMA_VERSION_CURRENT = "1.1";
    public static final String SCHEMA_VERSION_1_1 = "1.1";
    public static final String SCHEMA_VERSION_1_0 = "1.0";

    public static final String VP_SCHEMA_LOCATION_1_1 = "current.verifiable.presentation.schema.location";
    public static final String VP_SCHEMA_LOCATION_1_0 = "verifiable.presentation.schema.location_1_0";
    public static final String CREDENTIAL_SCHEMA_LOCATION_1_1 = "current.credential.schema.location";
    public static final String CREDENTIAL_SCHEMA_LOCATION_1_0 = "credential.schema.location_1_0";

    public static final String FRONTEND_CONTEXT_VARIABLE = "frontend_context";
    public static final String BASE_HREF_VARIABLE = "base_href";
    public static final String[] LEGACY_SCHEMA_LOCATIONS = {"epass_credential_schema_-xsd.xsd"};

    public enum Environment {
        LOCAL, DEV, QA, TEST, ACC, PROD
    }

    public class Defaults {

        public static final String XML_IDENTIFIER_PREFIX = "urn:epass:default:";
        public static final String XML_CRED_UUID_PREFIX = "urn:credential:";
        public static final String DEFAULT_LOCALE = "en";
        public static final String CREDENTIAL_DEFAULT_PREFIX = "Credential-";
        public static final String DIPLOMA_DEFAULT_PREFIX = "Diploma-";
        public static final String DIPLOMA_PAGE_SIZE = "A4 portait";
        public static final String DIPLOMA_PAGE_MARGINS = "-2.5mm";
        public static final String DEFAULT_MAILTO = "mailto:";
        //Refresh time of CRL in seconds (one week)
        public static final long CRL_REFRESH = 1 * 60 * 60 * 24;
        //Refresh time of OSCP in seconds (first number)
        public static final long OCSP_REFRESH = 3 * 60;
        public static final String SIGNATURE_LEVEL = "XAdES-BASELINE-LT";
        public static final String DIGEST_ALGORITHM = "SHA256";
        public static final String ENCRYPTION_ALGORITHM = "RSA";
    }

    public class DSS {
        public static final String SIGNATURE_LEVEL = "dss.signature.level";
        public static final String CERT_PATH = "dss.cert.path";
        public static final String DSS_TSP = "dss.tsp";
        public static final String LOTL_SOURCE = "dss.lotl.source";
        public static final String DIGEST_ALGORITHM_NAME = "digest.algorithm.name";
        public static final String DIGEST_ALGORITHM = "digest.algorithm";
        public static final String ENCRYPTION_ALGORITHM = "encryption.algorithm";
    }

    public class XML {
        public static final String XML_SIGNATURE_XPATH = "/eup:europassCredential/eup:proof";
    }

    public class Database {
        public static final String TARGET_DATABASE = "datasource.db.target-database";
    }

    public class Security {
        public static final String MOCK_USER_ACTIVE = "oidc.mock.user.active";
        public static final String MOCK_USER_INFO = "oidc.mock.user.info";
        public static final String POST_LOGOUT_URL = "oidc.post.logout.url";
        public static final String LOGIN_FILTER_URL = "oidc.login.url";
        public static final String WALLET_CLIENT_ID = "oidc.wallet.client.id";
        public static final String USE_TOKEN_EXCHANGE = "oidc.use.token.exchange";
        public static final String CLIENT_SECRET = "oidc.client.secret";
        public static final String CLIENT_ID = "oidc.client.id";
        public static final String REDIRECT_URL = "oidc.redirect.url";
        public static final String LOGIN_URL = "oidc.login.url";
        public static final String IDP_URL = "oidc.idp.url";
        public static final String INTROSPECTION_URL = "oidc.idp.introspection.url";
        public static final String INVALID_SESSION_URL = "oidc.invalid.session.url";
        public static final String EXPIRED_SESSION_REDIRECT_URL = "session.expired.redirect.url";
        public static final String SUCCESS_DEFAULT_URL = "oidc.success.default.url";
        public static final String SCOPES = "oidc.scopes";
        public static final String AUTH_ENDPOINT_METHOD = "oidc.endpoint.auth.method";
        public static final String CODE_CHALLENGE_METHOD = "oidc.code.challenge.method";
        public static final String SIGNING_ALG = "oidc.signing.alg";
        public static final String AUTH_REQUEST_URL = "oidc.auth.request.url";
        public static final String IDP_END_SESSION_URL = "oidc.idp.end.session.url";
    }

    public class Mail {
        public static final String ENCODE_MAIL_ATTACHMENT = "encode.mail.attachment";
    }

    public class Front {
        public static final String API_BASE_URL = "api.base.url";
        public static final String HTML_BASE_HREF = "html.base.href";
    }


}
