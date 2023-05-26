package eu.europa.ec.empl.edci.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import javax.servlet.ServletContext;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ThymeleafUtil {

    @Autowired
    ServletContext servletContext;

    public ThymeleafUtil() {

    }

    /**
     * Generates a HTML from the thymeleaf template String and context passed by parameters.
     *
     * @param stringTemplate string thymeleaf template (not a path to a template file)
     * @param context        Context containing variables used in the conversion process.
     * @param labels         properties of the labels used in the template to HTML process. First ones have higher priority over les the last ones.
     * @return html
     */
    public String processTemplate(String stringTemplate, Context context, Properties... labels) {

        TemplateEngine templateEngine = new TemplateEngine();

        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setDialect(new SpringStandardDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.setTemplateResolver(templateResolver);

        Set<IMessageResolver> messageResolverList = new LinkedHashSet<>();
        for (Properties prop : labels) {
            if (prop != null || !prop.isEmpty()) {
                StandardMessageResolver messageResolver = new StandardMessageResolver();
                messageResolver.setDefaultMessages(prop);
                messageResolverList.add(messageResolver);
            }
        }

        templateEngine.setMessageResolvers(messageResolverList);

        String html = templateEngine.process(stringTemplate, context);

        return html;

    }

    /**
     * Generates a HTML from the thymeleaf template String and context passed by parameters.
     *
     * @param templateFileName string thymeleaf template (a path to a template file without the extension. Pe.: "verifiable_diploma_template")
     * @param context        Context containing variables used in the conversion process.
     * @param labels         properties of the labels used in the template to HTML process. First ones have higher priority over les the last ones.
     * @return html
     */
    public String processTemplateFromWebInf(String templateFileName, Context context, Properties... labels) {

        TemplateEngine templateEngine = new TemplateEngine();

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setSuffix(".html");
        templateResolver.setPrefix("WEB-INF/pdf/templates/");

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setDialect(new SpringStandardDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.setTemplateResolver(templateResolver);

        Set<IMessageResolver> messageResolverList = new LinkedHashSet<>();
        for (Properties prop : labels) {
            if (prop != null || !prop.isEmpty()) {
                StandardMessageResolver messageResolver = new StandardMessageResolver();
                messageResolver.setDefaultMessages(prop);
                messageResolverList.add(messageResolver);
            }
        }

        templateEngine.setMessageResolvers(messageResolverList);

        String html = templateEngine.process(templateFileName, context);

        return html;

    }
}
