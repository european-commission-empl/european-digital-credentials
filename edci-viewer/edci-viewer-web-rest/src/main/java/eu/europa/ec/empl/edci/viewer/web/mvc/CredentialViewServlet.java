package eu.europa.ec.empl.edci.viewer.web.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.empl.edci.constants.EuropassConstants;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.util.DiplomaUtils;
import eu.europa.ec.empl.edci.viewer.common.Constants;
import eu.europa.ec.empl.edci.viewer.service.ViewerConfigService;
import eu.europa.ec.empl.edci.viewer.web.mapper.EuropassCredentialDetailRestMapper;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassCredentialDetailView;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassCredentialFullView;
import eu.europa.ec.empl.edci.viewer.web.model.EuropassDiplomaView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;

//ToDo -> Delete legacy class?
@Controller
@Api(tags = {
        "mvc"
})
public class CredentialViewServlet {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CredentialViewServlet.class);

//    private final static Logger _log = LoggerFactory.getLogger(CredentialViewServlet.class);

    @Inject
    private DiplomaUtils europassCredentialViewerUtils;

    @Autowired
    private EuropassCredentialDetailRestMapper europassCredentialDetailRestMapper;
    @Autowired
    private ViewerConfigService viewerConfigService;


    @ApiOperation("get the real JSON?")
    @PostMapping(value = "/preview/view")
    public ModelAndView previewJSON(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("ENTER CredentialViewServlet.doGet");
        ModelAndView modelAndView = new ModelAndView();
        String xml = null;

        String credId = req.getParameter("id");
        logger.debug(String.format("credential Id: %s", credId));
        boolean communicationSuccess = false;
        CloseableHttpClient httpClient = null;
        InputStream inputStreamObject = null;
        BufferedReader streamReader = null;
        try {
            httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(viewerConfigService.getString(Constants.CONFIG_PROPERTY_WALLET_DOWNLOAD_XML) + credId);
            httpGet.setHeader("Origin", "localhost");

            EntityBuilder builder = EntityBuilder.create();

            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                communicationSuccess = true;
                HttpEntity responseEntity = response.getEntity();
                inputStreamObject = responseEntity.getContent();
                streamReader = new BufferedReader(new InputStreamReader(inputStreamObject, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                xml = responseStrBuilder.toString();
            } else {
                logger.error("There has been an error: " + response.getStatusLine().getStatusCode());
            }
        } catch (FileNotFoundException e) {
            // TODO create error JSP
            logger.error(String.format("ERROR FILE NOT FOUND: %s", e.getMessage()));
        } catch (ClientProtocolException e) {
            // TODO create error JSP
            logger.error(String.format("ERROR ClientProtocolException: %s", e.getMessage()));
        } catch (IOException e) {
            // TODO create error JSP
            logger.error(String.format("ERROR I/O: %s", e.getMessage()));
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {

                }
            }
            if (inputStreamObject != null) {
                try {
                    inputStreamObject.close();
                } catch (IOException e) {

                }
            }
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {

                }
            }
        }
        if (communicationSuccess) {
            String credentialJSON = null;
            JAXBContext jaxbContext;
            //InputStream inputStream = null;
            EuropassCredentialDTO europassCredentialDTO = null;
            try {
                jaxbContext = JAXBContext.newInstance(EuropassCredentialDTO.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                //inputStream = file.getInputStream();

                europassCredentialDTO = (EuropassCredentialDTO) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

                EuropassCredentialFullView europassCredentialFullView = new EuropassCredentialFullView();
                EuropassDiplomaView europassDiplomaView = europassCredentialDetailRestMapper.toVO(europassCredentialViewerUtils.extractEuropassDiplomaDTO(europassCredentialDTO, EuropassConstants.DEFAULT_LOCALE));
                EuropassCredentialDetailView credentialDetailView = europassCredentialDetailRestMapper.toVO(europassCredentialDTO, EuropassConstants.DEFAULT_LOCALE);
                europassCredentialFullView.setDetail(credentialDetailView);
                europassCredentialFullView.setDiploma(europassDiplomaView);
                europassCredentialFullView.setId(credId);

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                credentialJSON = mapper.writeValueAsString(europassCredentialFullView);
                logger.trace(String.format("RECEIVED CREDENTIAL as JSON: %s", credentialJSON));

                //inputStream.close();
            } catch (IOException e) {
                logger.error(String.format("IOException: %s", e.getMessage()));
                logger.error(e);
            } catch (JAXBException e) {
                logger.error(String.format("JAXBException: %s", e.getMessage()));
                logger.error(e);
            } finally {
                /*if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        _log.error(String.format("IOException when closing inputStream: %s", e.getMessage()));
                        logger.error(e);
                    }
                }*/
            }
            //req.setAttribute("xml", "<textarea id='cred'>" + credentialJSON + "</textarea>");
            modelAndView.addObject("xml", "<textarea id='cred'>" + credentialJSON + "</textarea>");
        }

        modelAndView.setViewName("index_with_credential");
        //req.getRequestDispatcher("/index_with_credential.jsp").forward(req, resp);
        logger.debug("EXIT CredentialViewServlet.doGet");
        return modelAndView;
    }

}