package eu.europa.ec.empl.edci.dss.constants;

public class ESealMessageKeys {

    public class Exception {

        public class ESeal {
            public static final String INVALID_SIGNATURE_LEVEL = "exception.dss.invalid.signature.level";
            public static final String LOCAL_CERTIFICATE_NOT_DEFINED = "exception.local.certificate.not.defined";
            public static final String LOCAL_CERTIFICATE_NOT_FOUND = "local.cert.not.found";
            public static final String LOCAL_CERTIFICATE_BAD_PASSWORD = "local.cert.bad.password";
            public static final String CANNOT_READ_LOCAL_CERTIFICATE = "cannot.read.local.cert";
            public static final String CERTIFICATE_NOT_QSEAL_ERROR = "certificate.not.qseal.error";
        }

        public class Global {
            public static final String GLOBAL_INTERNAL_ERROR = "global.internal.error";
        }

    }

}
