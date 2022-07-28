package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Properties;

public class ThymeleafUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    private ThymeleafUtil thymeleafUtil;

    @Test
    public void processTemplate_ShouldGenerateHTML_fromValidThymeleaf() throws Exception {

        TemplateEngine templateEngine = new TemplateEngine();

        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("colour", "RED");

        Properties prop = new Properties();
        prop.put("global.yes", "Yes");

        String html = thymeleafUtil.processTemplate("<div class=\"content\">\n" +
                "                    <div class=\"title title-ribbon-warning ng-star-inserted\">\n" +
                "                        <span th:text=\"#{global.yes}\" />\n" +
                "                        <span th:text=\"${colour}\" />\n" +
                "                    </div>\n" +
                "                </div>", context, prop);

        Assert.assertTrue(html != null);
        Assert.assertTrue(html.contains("RED"));
        Assert.assertTrue(html.contains("Yes"));
    }

}
