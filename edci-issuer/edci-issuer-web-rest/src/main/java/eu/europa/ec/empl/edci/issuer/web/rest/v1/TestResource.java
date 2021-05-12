package eu.europa.ec.empl.edci.issuer.web.rest.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.Version;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.spec.ControlledListsOldService;
import eu.europa.ec.empl.edci.issuer.web.mapper.ConfigRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.ConfigView;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller(value = "v1.TestResource")
@RequestMapping(Version.V1 + "/test")
@ResponseStatus(HttpStatus.OK)
public class TestResource {

    @Autowired
    private OIDCAuthenticationFilter filter;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private ControlledListsOldService controlledListsService;

    @Autowired
    private ConfigRestMapper configRestMapper;


    private final Log logger = LogFactory.getLog(this.getClass());


    @GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ConfigView> getConfiguration() {
        return this.getConfigRestMapper().toView(this.getIssuerConfigService().getDatabaseConfiguration());
    }

    @PostMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ConfigView> updateConfiguration(@RequestBody List<ConfigView> configViews) {
        return this.getConfigRestMapper().toView(this.getIssuerConfigService().saveDatabaseConfiguration(this.getConfigRestMapper().toDTO(configViews)));
    }

    @PostMapping(value = "/config/default", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ConfigView> defaultConfiguration(@RequestBody List<String> keys) {
        return this.getConfigRestMapper().toView(this.getIssuerConfigService().populateDatabaseWithProperties(keys));
    }

    @RequestMapping(value = "/controlledListReload", method = RequestMethod.GET)
    @ResponseBody
    public String getControlledLists(@ApiParam(value = "entity") @RequestParam(value = "entity", required = false) String entity,
                                     @ApiParam(value = "clean") @RequestParam(value = "clean", required = false, defaultValue = "false") Boolean clean) {

        if (entity == null || entity.isEmpty()) {
            controlledListsService.loadControlledLists(clean);
        } else {
            ControlledList reload = ControlledList.getByName(entity);
            if (reload != null) {
                controlledListsService.loadControlledList(reload, clean);
            } else {
                return "Controlled list " + entity + " not found";
            }
        }

        return "OK";
    }


    @RequestMapping(value = "/oidc_userInfo", method = RequestMethod.GET)
    @ResponseBody
    public String getOIDCUserInfo() {
        String response = "";
        Object object = SecurityContextHolder.getContext().getAuthentication();
        if (object instanceof OIDCAuthenticationToken) {
            OIDCAuthenticationToken oidcAuthenticationToken = (OIDCAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            UserInfo userInfo = oidcAuthenticationToken.getUserInfo();
            response = "UserInfo: " + userInfo.getSource().toString();
            response += "Tokens: AccessToken { " + oidcAuthenticationToken.getAccessTokenValue() + " } IDToken { " + oidcAuthenticationToken.getIdToken() + " } RefreshToken { " + oidcAuthenticationToken.getRefreshTokenValue() + " }";
        } else if (object instanceof AnonymousAuthenticationToken) {
            AnonymousAuthenticationToken anonymousAuthenticationToken = (AnonymousAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                response = "Anonymous: " + objectMapper.writeValueAsString(anonymousAuthenticationToken);
            } catch (Exception e) {
                response = "Anonymous: " + anonymousAuthenticationToken.toString();
            }

        }


        return response;
    }


    public IssuerConfigService getIssuerConfigService() {
        return issuerConfigService;
    }

    public void setIssuerConfigService(IssuerConfigService issuerConfigService) {
        this.issuerConfigService = issuerConfigService;
    }

    public ConfigRestMapper getConfigRestMapper() {
        return configRestMapper;
    }

    public void setConfigRestMapper(ConfigRestMapper configRestMapper) {
        this.configRestMapper = configRestMapper;
    }
}
