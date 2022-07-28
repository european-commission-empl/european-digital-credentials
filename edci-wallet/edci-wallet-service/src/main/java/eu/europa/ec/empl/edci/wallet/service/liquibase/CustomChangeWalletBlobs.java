package eu.europa.ec.empl.edci.wallet.service.liquibase;

import eu.europa.ec.empl.edci.context.SpringApplicationContext;
import eu.europa.ec.empl.edci.datamodel.model.base.Localizable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Content;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.liquibase.EDCIAbstractCustomTask;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.CredentialLocalizableInfoService;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomChangeWalletBlobs extends EDCIAbstractCustomTask {

    private final static String VALIDATION_CHECK_QUERY = "SELECT COUNT(*) AS COUNT FROM CREDENTIAL_LOCALIZABLE_INFO";
    //Robert's query not working anymore? "select sign(info_total- (SELECT COUNT(1) FROM credential_t)) AS COUNT from (SELECT COUNT(1) as info_total FROM ( SELECT DISTINCT(c.cred_id) FROM credential_t c JOIN credential_localizable_info   cl ON cl.cred_id = c.cred_id))";
    private static final Logger logger = LogManager.getLogger(CustomChangeWalletBlobs.class);

    protected Logger getLogger() {
        return logger;
    }


    @Override
    protected String getValidationSQL() {
        return VALIDATION_CHECK_QUERY;
    }

    @Override
    protected long getValidationSQLResult() {
        return 0;
    }

    @Override
    public void executeChange(Database database) throws Exception {
        System.out.println("STARTING CUSTOM CHANGE PROCESS");
        //Get Required Beans
        BeanFactory beanFactory = SpringApplicationContext.getBeanFactory();
        CredentialRepository credentialRepository = beanFactory.getBean(CredentialRepository.class);
        CredentialLocalizableInfoService credentialLocalizableInfoService = beanFactory.getBean(CredentialLocalizableInfoService.class);
        System.out.println("ACCQUIRED NECESSARY BEANS");

        long credentialResultSize = 0;

        //Get JDBC connection
        JdbcConnection dbConn = (JdbcConnection) database.getConnection();
        try (Statement statement = dbConn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS COUNT FROM CREDENTIAL_T")) {
            resultSet.next();
            credentialResultSize = resultSet.getInt("COUNT");
        }

        System.out.println("CURRENT CREDENTAIL RESULTS : ".concat(String.valueOf(credentialResultSize)));

        //esecute statment and get resultset in try-with-resources
        try (Statement statement = dbConn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT * FROM CREDENTIAL_T");) {
            while (resultSet.next()) {
                //Get Required column data
                byte[] titleBytes = resultSet.getBytes("TITLE");
                byte[] descriptionBytes = resultSet.getBytes("DESCRIPTION");
                byte[] credentialBytes = resultSet.getBytes("CREDENTIAL_XML");

                //For possible inconsistencies in dev environments
                if (titleBytes != null && credentialBytes != null) {
                    Long credId = resultSet.getLong("CRED_ID");
                    XPathFactory xpathfactory = XPathFactory.newInstance();
                    XPath xpath = xpathfactory.newXPath();

                    System.out.println("Parsing title : ".concat(new String(titleBytes, StandardCharsets.UTF_8)));
                    System.out.println("Parsing description : ".concat(new String(descriptionBytes, StandardCharsets.UTF_8)));
                    System.out.println("Parsing credential : ".concat(new String(credentialBytes, StandardCharsets.UTF_8)));

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(false);
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    Document titleDoc = builder.parse(new ByteArrayInputStream(titleBytes));
                    Document descriptionDoc = builder.parse(new ByteArrayInputStream(descriptionBytes));
                    Document credentialDoc = builder.parse(new ByteArrayInputStream(credentialBytes));

                    Text title = new Text();
                    title.setContents(this.getLocalizableContents(titleDoc, xpath, "/text"));

                    Note description = new Note();
                    description.setContents(this.getLocalizableContents(descriptionDoc, xpath, "/note"));

                    Text typeName = new Text();
                    typeName.setContents(this.getLocalizableContents(credentialDoc, xpath, "/europassCredential/type/targetName"));

                    System.out.println("Found type ".concat(typeName.toString()));

                    Set<String> availableLanguages = this.getAllUniqueLanguages(title, description, typeName);
                    System.out.println("All available Languages: ".concat(availableLanguages.toString()));
                    CredentialDAO credentialDAO = credentialRepository.findById(credId).orElse(null);

                    for (String language : availableLanguages) {
                        CredentialLocalizableInfoDAO credentialLocalizableInfoDAO = new CredentialLocalizableInfoDAO();
                        credentialLocalizableInfoDAO.setCredentialDAO(credentialDAO);
                        credentialLocalizableInfoDAO.setLang(language);
                        credentialLocalizableInfoDAO.setTitle(title.getLocalizedString(language).orElse(null));
                        credentialLocalizableInfoDAO.setDescription(description.getLocalizedString(language).orElse(null));
                        credentialLocalizableInfoDAO.setCredentialType(typeName.getLocalizedString(language).orElse(null));
                        credentialLocalizableInfoDAO.setCredentialDAO(credentialDAO);

                        credentialDAO.getCredentialLocalizableInfoDAOS().add(credentialLocalizableInfoDAO);

                        System.out.println("creating CredentialLocalizableInfoDAO: " + credentialLocalizableInfoDAO.getLang() + "/" + credentialLocalizableInfoDAO.getTitle());
                        credentialLocalizableInfoService.save(credentialLocalizableInfoDAO);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }

        long credentialLocalizableResults = credentialLocalizableInfoService.getRepository().count();


        System.out.println("credentialResultSize: ".concat(String.valueOf(credentialResultSize)).concat(" credentialLocalizableSize: ".concat(String.valueOf(credentialLocalizableResults))));
        if (credentialResultSize > credentialLocalizableResults) {
            throw new Exception("ERROR, THERE ARE MORE CREDENTIALS IN CREDENTIAL_T TABLE THAN IN CREDENTIAL_LOCALIZABLE_INFO");
        }
    }

    @Override
    public void executeRollBack(Database database) throws Exception {
        System.out.println("ROLLING BACK CUSTOM CHANGE LOGIC GOES HERE");
    }

    //utility methods

    public Set<String> getAllUniqueLanguages(Localizable... localizables) {
        List<Localizable> nonEmptyLocalizables = Arrays.stream(localizables).filter(localizable -> localizable != null && localizable.getContents() != null && !localizable.getContents().isEmpty()).collect(Collectors.toList());
        Set<String> languages = nonEmptyLocalizables.stream()
                .map(localizable -> localizable.getContents())
                .flatMap(contents -> contents.stream())
                .map(content -> content.getLanguage())
                .collect(Collectors.toSet());
        return languages;
    }

    public List<Content> getLocalizableContents(Document doc, XPath xpath, String expression) {

        List<Content> contents = null;

        try {
            XPathExpression expr = xpath.compile(expression);
            Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);

            if (result == null) {
                System.out.println("Property not found using expression " + expression);
            } else {
                contents = getLocalizableContents(result);
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return contents;
    }

    private List<Content> getLocalizableContents(Element node) {

        List<Content> contents = null;

        try {
            List<Node> nodeList = IntStream.range(0, node.getChildNodes().getLength())
                    .mapToObj(node.getChildNodes()::item).filter(n -> "text".equalsIgnoreCase(n.getNodeName())).collect(Collectors.toList());

            contents = nodeList.stream().map(n -> new Content(n.getTextContent(), n.getAttributes().getNamedItem("lang").getNodeValue())).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error(e);
        }

        return contents;
    }
}
