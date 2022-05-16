package eu.europa.ec.empl.edci.dss.config;


import eu.europa.esig.dss.service.crl.JdbcCacheCRLSource;

/**
 * Extends JdbcCacheCRLSource to replace SQL_INIT_CREATE_TABLE with Oracle compatible datatypes
 */
public class EdciJdbcCacheCRLSource extends JdbcCacheCRLSource {

    private static final String SQL_INIT_CREATE_TABLE_FIXED_ORA = "CREATE TABLE CACHED_CRL (ID CHAR(40), DATA BLOB, ISSUER BLOB)";
    private static final String SQL_INIT_CREATE_TABLE_FIXED_GENERIC = "CREATE TABLE CACHED_CRL (ID CHAR(40), DATA BLOB, ISSUER BLOB)";

    private String database;

    public EdciJdbcCacheCRLSource() {
        this.database = "Oracle11";
    }

    public EdciJdbcCacheCRLSource(String database) {
        this.database = database;
    }

    @Override
    protected String getCreateTableQuery() {
        if (this.database != null && this.database.startsWith("Oracle")) {
            return SQL_INIT_CREATE_TABLE_FIXED_ORA;
        } else {
            return SQL_INIT_CREATE_TABLE_FIXED_GENERIC;
        }

    }

}

