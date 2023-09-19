package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoElementPayload;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoSearchResults;
import eu.europa.ec.empl.edci.repository.util.PageParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EscoBridgeService {

    private static final Logger logger = LogManager.getLogger(EscoBridgeService.class);

    public enum EscoList {

        OCCUPATION("http://data.europa.eu/esco/concept-scheme/occupations", "occupation"),
        SKILL("http://data.europa.eu/esco/concept-scheme/skills", "skill");

        private String targetFrameWorkUrl;
        private String type;

        public String getTargetFrameWorkUrl() {
            return this.targetFrameWorkUrl;
        }

        public String getType() {
            return this.type;
        }

        private EscoList(String targetFrameWorkUrl, String type) {
            this.targetFrameWorkUrl = targetFrameWorkUrl;
            this.type = type;
        }

        private static boolean contains(String targetFrameWorkUrl) {
            return Arrays.stream(EscoList.values()).anyMatch(item -> item.getTargetFrameWorkUrl().equals(targetFrameWorkUrl));
        }

        private static EscoList getEscoList(String targetFrameWorkUrl) {
            return Arrays.stream(EscoList.values()).filter(item -> item.getTargetFrameWorkUrl().equals(targetFrameWorkUrl)).findFirst().orElse(null);
        }

    }

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    public boolean isEscoList(String targetFrameworkUrl) {
        return EscoList.contains(targetFrameworkUrl);
    }

    public EscoList getEscoList(String targetFrameworkUrl) {
        return EscoList.getEscoList(targetFrameworkUrl);
    }

    public static EscoList getEscoListByType(String type) {
        return Arrays.stream(EscoList.values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    public EscoSearchResults searchEsco(
            Class<? extends EscoSearchResults> responseType,
            String type,
            String search,
            String lang,
            Integer page,
            Integer size) {

        RestTemplate restTemplate = new RestTemplate();

        final String url = "https://ec.europa.eu/esco/api/search";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("type", type)
                .queryParam("text", search)
                .queryParam("language", lang)
                .queryParam("full", false)
                .queryParam("limit", size)
                .queryParam("offset", page);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                responseType);

        return (EscoSearchResults) response.getBody();
    }

    @Cacheable("CL_esco")
    public EscoElementPayload searchEsco(
            Class<? extends EscoElementPayload> responseType,
            String type,
            String lang,
            String uri) {

        RestTemplate restTemplate = new RestTemplate();

        final String url = "https://ec.europa.eu/esco/api/resource/" + type;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("language", lang)
                .queryParam("uri", uri);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                responseType);

        return (EscoElementPayload) response.getBody();
    }

    public Page<CodeDTDAO> searchEscoElements(
            String type,
            String search,
            String lang,
            List<String> retrieveLangs,
            Integer page,
            Integer size) {

        EscoSearchResults results = searchEsco(EscoSearchResults.class, type, search, lang, page, size);

        PageParam pageParam = new PageParam(page, size);

        Page pageResult = new PageImpl<CodeDTDAO>(
                results.getEmbedded().getResults().stream().map(skill -> controlledListsMapper.toCodeDAOESCO(skill, getEscoListByType(type).getTargetFrameWorkUrl(), retrieveLangs)).collect(Collectors.toList()),
                pageParam.toPageRequest(),
                results.getTotal());


        return pageResult;

    }

    @Cacheable("CL_esco")
    public List<CodeDTDAO> searchEscoElements(
            String type,
            String lang,
            List<String> retrieveLangs,
            String... uris) {

        List<CodeDTDAO> skosList = new ArrayList<>();

        for (String uri : uris) {

            EscoElementPayload result = searchEsco(EscoElementPayload.class, type, lang, uri);

            skosList.add(controlledListsMapper.toCodeDAOESCO(result, getEscoListByType(type).getTargetFrameWorkUrl(), retrieveLangs));

        }

        return skosList;

    }

}
