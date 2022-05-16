package eu.europa.ec.empl.edci.dss.config;

import eu.europa.esig.dss.service.ocsp.JdbcCacheOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"eu.europa.ec.empl.edci.dss.config", "eu.europa.ec.empl.edci.dss.validation"})
public class DSSBeanConfig {

    @Autowired
    private EdciJdbcCacheCRLSource cachedCRLSource;

    @Autowired
    private JdbcCacheOCSPSource cachedOCSPSource;

    @Autowired
    private DataLoader dataLoader;

    private static String keystoreResourcePath = "src/main/resources/validation/keystore.p12";


  /*  @Bean
    public CertificateVerifier certificateVerifier() throws Exception {
        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        certificateVerifier.setCrlSource(cachedCRLSource);
        certificateVerifier.setOcspSource(cachedOCSPSource);
        certificateVerifier.setDataLoader(dataLoader);

        // TODO is this ok?
        certificateVerifier.setCheckRevocationForUntrustedChains(true);

        return certificateVerifier;
    }

    @Bean
    public TrustedListsCertificateSource trustedListsCertificateSource() {
        return new TrustedListsCertificateSource();
    }

    @Bean
    public KeyStoreCertificateSource ojContentKeyStore() {
        return new KeyStoreCertificateSource(DSSValidationUtils.class.getResourceAsStream(keystoreResourcePath), "PKCS12",
                "dss-password");
    }*/


}
