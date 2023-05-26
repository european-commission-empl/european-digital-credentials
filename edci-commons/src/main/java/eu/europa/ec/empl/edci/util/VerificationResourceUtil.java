package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.model.external.VerificationCheckReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class VerificationResourceUtil implements InitializingBean {

    public static final Logger logger = LogManager.getLogger(VerificationResourceUtil.class);
    public final String VALIDATE_ENDPOINT = "/validate";

    @Autowired
    private BaseConfigService configService;

    private String verificationBaseURL;

    @Override
    public void afterPropertiesSet() throws Exception {
        String verificationBaseURL = configService.getString(EDCIConfig.Verification.VERIFICATION_BASE_URL, EDCIConfig.Defaults.VERIFICATION_BASE_URL);
        verificationBaseURL = verificationBaseURL.replaceAll("/+$", "");
        this.setVerificationBaseURL(verificationBaseURL);
    }

    public List<VerificationCheckReport> getVerificationCheckReport(MultipartFile file, String locale) {
        EDCIRestRequestBuilder edciRestRequestBuilder = new EDCIRestRequestBuilder(HttpMethod.POST,
                this.getVerificationBaseURL().concat(VALIDATE_ENDPOINT));

        return null;
    }

    public String getVerificationBaseURL() {
        return verificationBaseURL;
    }

    public void setVerificationBaseURL(String verificationBaseURL) {
        this.verificationBaseURL = verificationBaseURL;
    }
}
