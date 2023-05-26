package eu.europa.ec.empl.edci.service;


import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIMailService {
    private static final Logger logger = LogManager.getLogger(EDCIMailService.class);

    @Autowired
    private Validator validator;

    public static Session mailSession = null;

    @Autowired
    private Environment env;

    /**
     * @param directory  directory of the template
     * @param template   name of the template, without locale or extension
     * @param subject    subject of the email
     * @param wildCards  map with wildcards to be replaced in format key-value
     * @param recipients list of address to send the email
     * @param attachment Path of the attachment file (optional)
     * @param fileName   Name of the attachment File (optional)
     */
    public void sendTemplatedEmail(String directory, String template, String subject, Map<String, String> wildCards,
                                   List<String> recipients, String locale, @Nullable byte[] attachment, @Nullable String fileName, @Nullable byte[] thumbnail) throws EDCIException {
        String htmlBody = this.replaceMailWildCards(getHtmlBodyFromFile(directory, template, locale), wildCards);
        try {
            //Create Message
            MimeMessage message = new MimeMessage(this.getSession());
            message.setRecipients(Message.RecipientType.TO, parseInternetAddresses(recipients).toArray(new InternetAddress[0]));
            message.setSubject(subject, StandardCharsets.UTF_8.name());
            MimeMultipart multiPart = new MimeMultipart();

            //Add Body Content
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlBody, MediaType.TEXT_HTML_VALUE.concat("; charset=").concat(StandardCharsets.UTF_8.name().toLowerCase()));
            multiPart.addBodyPart(messageBodyPart);

            //Add attachment if required
            if (validator.notEmpty(attachment)) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                String fileType = URLConnection.guessContentTypeFromName(fileName) == null ? EDCIConstants.JSON.EXTENSION_JSON_LD : URLConnection.guessContentTypeFromName(fileName);
                ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(attachment, "application/octet-stream");
                attachmentBodyPart.setDataHandler(new DataHandler(byteArrayDataSource));
                attachmentBodyPart.setFileName(fileName);
                multiPart.addBodyPart(attachmentBodyPart);
            }

            if (thumbnail != null) {
                BodyPart imagePart = new MimeBodyPart();
                ByteArrayDataSource imageDataSource = new ByteArrayDataSource(thumbnail, "image/jpeg");

                imagePart.setDataHandler(new DataHandler(imageDataSource));
                imagePart.setHeader("Content-ID", "<thumbnail_diploma>");
                imagePart.setFileName("thumbnail.jpeg");
                multiPart.addBodyPart(imagePart);
            }

            //Send message
            message.setContent(multiPart);
            Transport.send(message);
        } catch (NamingException ne) {
            throw new EDCIException().addDescription("could not get mail session");
        } catch (EDCIException ede) {
            logger.error(ede);
            throw ede;
        } catch (MessagingException me) {
            logger.error(me);
            throw new EDCIException().addDescription("Could not send email");
        }

    }

    private Session getSession() throws NamingException {
        if (mailSession == null) {

            this.mailSession = null;

            Properties props = new Properties();
            props.put("mail.smtp.host", env.getProperty("mail.host"));
            props.put("mail.smtp.port", env.getProperty("mail.port"));
            props.put("mail.smtp.auth", env.getProperty("mail.auth"));
            props.put("mail.smtp.starttls.enable", env.getProperty("mail.starttls.enable"));
            props.put("mail.smtp.starttls.required", env.getProperty("mail.starttls.enable"));
            props.put("mail.transport.protocol", env.getProperty("mail.transport.protocol"));

            if (!StringUtils.isEmpty(env.getProperty("mail.from"))) {
                props.put("mail.smtp.from", env.getProperty("mail.from"));
            }
            if (!StringUtils.isEmpty(env.getProperty("mail.tls.protocols"))) {
                props.put("mail.smtp.ssl.protocols", env.getProperty("mail.tls.protocols"));
            }

            if (env.getProperty("mail.auth", Boolean.class)) {
                String fromEmail = env.getProperty("mail.user");
                String password = env.getProperty("mail.pass");
                Authenticator auth = new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                };
                this.mailSession = Session.getInstance(props, auth);
            } else {
                this.mailSession = Session.getInstance(props);
            }

        }

        return mailSession;
    }

    private String getHtmlBodyFromFile(String directory, String fileName, String locale) {
        ClassPathResource template = new ClassPathResource(
                directory.concat(fileName)
                        .concat("_")
                        .concat(locale)
                        .concat(".html"));

        InputStream inputStream = null;
        String htmlText;

        try {
            inputStream = template.getInputStream();
            htmlText = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            throw new EDCIException().addDescription(String.format("Could not get Mail template %s", template.getPath()));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }

        return htmlText;
    }

    private String replaceMailWildCards(String content, Map<String, String> wildCards) {
        for (Map.Entry<String, String> entry : wildCards.entrySet()) {
            content = content.replaceAll(Pattern.quote(entry.getKey()), entry.getValue());
        }
        return content;
    }


    private List<InternetAddress> parseInternetAddresses(List<String> addresses) throws EDCIException {
        List<InternetAddress> internetAddresses = new ArrayList<InternetAddress>();
        for (String address : addresses) {
            try {
                internetAddresses.addAll(Arrays.asList(InternetAddress.parse(address)));
            } catch (AddressException e) {
                throw new EDCIException().addDescription(String.format("Could not parse address %s", address));
            }
        }
        return internetAddresses;
    }
}
