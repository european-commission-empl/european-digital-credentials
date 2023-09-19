package eu.europa.ec.empl.edci.dss.constants;

import eu.europa.esig.dss.enumerations.*;

import java.util.Arrays;
import java.util.List;

public abstract class ESealConfig {

    public static class Defaults {
        public static final List<SignatureLevel> SIGNATURE_LEVELS_XML =
                Arrays.asList(SignatureLevel.XAdES_T, SignatureLevel.XAdES_LT, SignatureLevel.XAdES_BASELINE_B,
                        SignatureLevel.XAdES_BASELINE_T, SignatureLevel.XAdES_BASELINE_LT, SignatureLevel.XAdES_BASELINE_LTA);
        public static final List<SignatureLevel> SIGNATURE_LEVELS_JSON = Arrays.asList(SignatureLevel.JAdES_BASELINE_LTA);
        public static final String DIGEST_ALGORITHM_SIGN = DigestAlgorithm.SHA256.getName();
        public static final List<DigestAlgorithm> DIGEST_ALGORITHM_VALIDATION = Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA384, DigestAlgorithm.SHA512);
        public static final String ENCRYPTION_ALGORITHM = EncryptionAlgorithm.RSA.name();
        public static final boolean JOB_ONLINE_REFRESH = false;
        public static final Boolean ADV_QSEAL_ONLY = true;
        public static final String LOTL_SOURCE = "https://ec.europa.eu/tools/lotl/eu-lotl.xml";
        public static final long OCSP_REFRESH = 3 * 60;
        public static final long CRL_REFRESH = 1 * 60 * 60 * 24;
        public static final String TARGET_DATABASE = "MySQL";

        public static class Jades {
            public static final String SIGNATURE_FORM = SignatureForm.JAdES.name();
            public static final String SIGNATURE_LEVEL = SignatureLevel.JAdES_BASELINE_LTA.toString();
            public static final String SIGNATURE_PACKAGING = SignaturePackaging.ENVELOPING.name();
            public static final Boolean PAYLOAD_BASE64 = false;
            public static final String JWS_SERIALIZATION_TYPE = JWSSerializationType.JSON_SERIALIZATION.name();
        }

        public static class Xades {
            public static final String SIGNATURE_FORM = SignatureForm.XAdES.name();
            public static final String SIGNATURE_LEVEL = SignatureLevel.XAdES_BASELINE_LTA.toString();
            public static final String SIGNATURE_PACKAGING = SignaturePackaging.ENVELOPED.name();
        }
    }

    public class Properties {
        public static final String SIGNATURE_FORM = "eseal.sealing.signature.form";
        public static final String SIGNATURE_LEVEL = "eseal.sealing.signature.level";
        public static final String SIGNATURE_LEVEL_LIST_JSON = "eseal.sealing.signature.level.list.json";
        public static final String SIGNATURE_LEVEL_LIST_XML = "eseal.sealing.signature.level.list.xml";
        public static final String SIGNATURE_PACKAGING = "eseal.sealing.signature.packaging";
        public static final String DIGEST_ALGORITHM = "eseal.sealing.digest.algorithm";
        public static final String PAYLOAD_BASE64 = "eseal.sealing.payload.base64";
        public static final String ENCRYPTION_ALGORITHM = "eseal.sealing.encryption.algorithm";
        public static final String JWS_SERIALIZATION_TYPE = "eseal.sealing.jws.serialization.type";
        public static final String DSS_PATH = "http://dss.nowina.lu/pki-factory/tsa/good-tsa";
        public static final String JOB_ONLINE_REFRESH = "eseal.job.online.refresh";
        public static final String ADV_QSEAL_ONLY = "eseal.advanced.qseal.only";
        public static final String ESEAL_CQ_ALLOWED = "eseal.certificate.qualification.allowed";
        public static final String LOTL_SOURCE = "eseal.dss.lotl.source";
        public static final String OCSP_REFRESH = "eseal.oscp.refresh";
        public static final String CRL_REFRESH = "eseal.crl.refresh";
        public static final String TARGET_DATABASE = "eseal.datasource.db.target-database";
        public static final String DRIVER_CLASS_NAME = "eseal.datasource.db.driverClassName";
        public static final String DATABASE_URL = "eseal.datasource.db.url";
        public static final String DATABASE_USERNAME = "eseal.datasource.db.username";
        public static final String DATABASE_PASSWORD = "eseal.datasource.db.password";
    }


    public class Path {
        public static final String ESEAL_FILE = "file:${edci.properties.base}/eseal_core.properties";
        public static final String PROXY_FILE = "file:${edci.properties.base}/proxy.properties";
    }
}
