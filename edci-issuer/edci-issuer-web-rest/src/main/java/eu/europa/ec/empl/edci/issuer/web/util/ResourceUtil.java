package eu.europa.ec.empl.edci.issuer.web.util;

import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ResourceUtil {

    public HttpHeaders prepareHttpHeadersForXLSFileDownload(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (fileName.endsWith(IssuerConstants.EXTENSION_XLSM)) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, eu.europa.ec.empl.edci.constants.MediaType.APPLICATION_XLSM_VALUE);
        } else if (fileName.endsWith(IssuerConstants.EXTENSION_XLSX)) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, eu.europa.ec.empl.edci.constants.MediaType.APPLICATION_XLSX_VALUE);
        } else if (fileName.endsWith(IssuerConstants.EXTENSION_XLS)) {
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, eu.europa.ec.empl.edci.constants.MediaType.APPLICATION_XLS_VALUE);
        }

        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return httpHeaders;
    }
}
