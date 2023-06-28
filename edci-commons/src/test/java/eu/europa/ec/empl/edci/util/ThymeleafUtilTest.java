package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import integration.eu.europa.ec.empl.edci.util.JsonLdUtilITest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Properties;

public class ThymeleafUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    private ThymeleafUtil thymeleafUtil;

    @Test
    public void processTemplate_ShouldGenerateHTML_fromValidThymeleaf() throws Exception {

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new Java8TimeDialect());

        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
        JsonLdUtilITest jsonLdUtilITest = new JsonLdUtilITest();
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getVerifiableCredentialDTO();

        /*MediaObject mediaObject = diplomaUtils.getMediaObject(EDCIConstants.DEFAULT_VIEWER_DIPLOMA_LOGO_IMG_PATH);
        MediaObjectDTO mediaObjectDTO = new MediaObjectDTO();
        mediaObjectDTO.setContent(mediaObject.getContent().toString());
        europeanDigitalCredentialDTO.getIssuer().setLogo(mediaObjectDTO);*/

        Context context = new Context();
        context.setVariable("credential", europeanDigitalCredentialDTO);

        Properties prop = new Properties();
        prop.put("diploma.msg.certifiesThat", "Certificado primo");

        String html = thymeleafUtil.processTemplate("<div style=\"font-style: normal; padding-bottom: 2rem; padding-top: 6rem;\">\n" +
                "    <div\n" +
                "            style=\"flex: 0 0 100%; max-width: 100%; display: block; text-align: center;\"\n" +
                "    >\n" +
                "        <img\n" +
                "                style=\"max-height: 4.5rem; width: auto;\"\n" +
                "                th:src=\"${'data:image/png;base64,' + credential.issuer.logo}\"\n" +
                "        />\n" +
                "        <br/>\n" +
                "        <p style=\"font-size: 18px; color: #525252;\"\n" +
                "            th:text=\"${credential.issuer.prefLabel}\">\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    <div style=\"flex: 0 0 100%; max-width: 100%;\">\n" +
                "        <p style=\"\n" +
                "        padding-left: 0.5rem !important;\n" +
                "        margin: 3rem 1em;\n" +
                "        text-align: center !important;\n" +
                "        font-weight: 700 !important;\n" +
                "        color: #004494 !important;\n" +
                "        text-decoration: underline;\n" +
                "        font-size: 31px;\n" +
                "      \"\n" +
                "            th:text=\"${credential.displayParameter.title}\">\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    <div style=\"flex: 0 0 100%; max-width: 100%;\">\n" +
                "        <p style=\"text-align: center; font-weight: 700 !important; font-size: 18px;\"\n" +
                "            th:text=\"${credential.issuer.prefLabel}\">\n" +
                "        </p>\n" +
                "        <p style=\"text-align: center; font-size: 18px;\"\n" +
                "           th:text=\"#{diploma.msg.certifiesThat}\">\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    <div style=\"flex: 0 0 100%; max-width: 100%;\">\n" +
                "        <p style=\"\n" +
                "        font-weight: 700 !important;\n" +
                "        font-size: 31px;\n" +
                "        margin-top: 2rem;\n" +
                "        margin-bottom: 2rem;\n" +
                "        color: #004494 !important;\n" +
                "        text-align: center;\n" +
                "        font-style: italic !important;\n" +
                "      \"\n" +
                "            th:text=\"${credential.credentialSubject.fullName}\">\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    <div\n" +
                "            style=\"\n" +
                "      flex: 0 0 100%;\n" +
                "      max-width: 100%;\n" +
                "      height: 7cm;\n" +
                "      margin: 0 auto;\n" +
                "      width: 87%;\n" +
                "      padding-bottom: 1rem !important;\n" +
                "    \"\n" +
                "    >\n" +
                "        <p style=\"white-space: pre-line;\n" +
                "                    font-size: 12px; text-align: center; font-weight: 300 !important;\"\n" +
                "            th:text=\"${credential.displayParameter.description}\">\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    <div style=\"flex: 0 0 100%; max-width: 100%; margin-left: 2.5rem;\">\n" +
                "        <p style=\"\n" +
                "        font-style: oblique;\n" +
                "        text-align: left;\n" +
                "        font-weight: 300 !important;\n" +
                "      \"\n" +
                "            th:text=\"${#temporals.format(credential.issued, 'dd/MM/yyyy')}\">\n" +
                "        </p>\n" +
                "    </div>\n" +
                "</div>\n", context, "en", prop);

        System.out.println(html);
        Assert.assertTrue(html != null);
        Assert.assertTrue(html.contains("Certificado primo"));

    }

}
