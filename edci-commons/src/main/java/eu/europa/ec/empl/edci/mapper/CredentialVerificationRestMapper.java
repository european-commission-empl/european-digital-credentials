package eu.europa.ec.empl.edci.mapper;

import com.google.common.collect.Maps;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.model.external.ConceptReport;
import eu.europa.ec.empl.edci.model.external.VerificationCheckReport;
import eu.europa.ec.empl.edci.model.view.VerificationCheckView;
import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.context.i18n.LocaleContextHolder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CredentialVerificationRestMapper {

    public static final Logger logger = LogManager.getLogger(CredentialVerificationRestMapper.class);

    List<VerificationCheckView> toVOList(List<VerificationCheckReport> verificationCheckDTOList, @Context String lang);

    @Mappings({
            @Mapping(source = "description", target = "descrAvailableLangs"),
            @Mapping(source = "longDescription", target = "longDescrAvailableLangs"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "longDescription", target = "longDescription")
    })
    VerificationCheckView toVO(VerificationCheckReport verificationCheckDTO, @Context String lang);

    default LinkFieldView toLinkCode(ConceptReport code) {

        if (code == null || code.getPrefLabel() == null) {
            return null;
        }

        LinkFieldView lfv = new LinkFieldView();

        try {
            lfv.setLink(URI.create(code.getId().toString()));
            lfv.setTitle(MultilangFieldUtil.getLiteralStringListOrAny(code.getPrefLabel(),
                    LocaleContextHolder.getLocale().getLanguage()));
            lfv.setTitleAvailableLangs(code.getPrefLabel());
        } catch (Exception e) {
            logger.error("Error mapping linkField. Leaving the object null", e);
        }

        return lfv;

    }

    default String toStringFromLiteralMap(LiteralMap map) {
        return map != null ? map.toString() : "";
    }

    default Map<String, String> toMapStringsFromLiteralMap(LiteralMap map) {
        return map != null ? Maps.transformValues(map, value -> value.get(0)) : null;
    }
}
