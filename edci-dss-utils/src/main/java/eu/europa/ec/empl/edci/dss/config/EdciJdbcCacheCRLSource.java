package eu.europa.ec.empl.edci.dss.config;


import eu.europa.esig.dss.service.crl.JdbcCacheCRLSource;

/**
 * Extends JdbcCacheCRLSource to replace SQL_INIT_CREATE_TABLE with Oracle compatible datatypes
 */
public class EdciJdbcCacheCRLSource extends JdbcCacheCRLSource {


    private static final String SQL_INIT_CREATE_TABLE_FIXED = "CREATE TABLE CACHED_CRL (ID CHAR(40), DATA BLOB, SIGNATURE_ALGORITHM VARCHAR(20), THIS_UPDATE TIMESTAMP, NEXT_UPDATE TIMESTAMP, EXPIRED_CERTS_ON_CRL TIMESTAMP, ISSUER BLOB, ISSUER_PRINCIPAL_MATCH NUMBER(1), SIGNATURE_INTACT NUMBER(1), CRL_SIGN_KEY_USAGE NUMBER(1), UNKNOWN_CRITICAL_EXTENSION NUMBER(1), SIGNATURE_INVALID_REASON VARCHAR(256))";

    @Override
    protected String getCreateTableQuery() {
        return SQL_INIT_CREATE_TABLE_FIXED;
    }

}

