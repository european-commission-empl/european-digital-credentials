package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class})
public interface BaseDAOMapper {

    default String toStringFromLiteralMap(LiteralMap map) {
        return map != null ? map.toString() : EDCIConstants.StringPool.STRING_EMPTY;
    }

    default LiteralMap toLiteralMap(TextDTDAO textDTDAO) {
        LiteralMap literalMap = new LiteralMap();

        if(textDTDAO == null) {
            return null;
        }

        textDTDAO.getContents().forEach(contentDTDAO -> literalMap.put(contentDTDAO.getLanguage(), contentDTDAO.getContent()));
        return literalMap;
    }

    default LiteralMap toLiteralMap(String literal, @Context String locale) {
        return new LiteralMap(locale, literal);
    }

    default LiteralMap toLiteralMap(ContentDTDAO text) {
        if (text == null) {
            return null;
        }

        return new LiteralMap(text.getLanguage(), text.getContent());
    }

    default LiteralMap toLiteralMap(List<ContentDTDAO> text) {
        if (text == null) {
            return null;
        }

        LiteralMap literalMap = new LiteralMap();

        for (ContentDTDAO contentDTDAO : text) {
            literalMap.putAll(toLiteralMap(contentDTDAO));
        }

        return literalMap;
    }

    default LiteralMap toLiteralMap(NoteDTDAO noteDTDAO) {
        if (noteDTDAO != null && noteDTDAO.getContents() != null) {
            LiteralMap literalMap = new LiteralMap();
            noteDTDAO.getContents().stream().forEach(contentDTDAO -> literalMap.put(contentDTDAO.getLanguage(), contentDTDAO.getContent()));
            return literalMap;
        } else {
            return null;
        }
    }

    default String toStringFromConcept(ConceptDTO conceptDTO) {
        return conceptDTO != null ? conceptDTO.toString() : null;
    }

    @Mappings({
            @Mapping(target = "notation", source = "targetNotation"),
            @Mapping(target = "prefLabel", source = "targetName"),
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "inScheme.id", source = "targetFrameworkURI"),
    })
    ConceptDTO toConceptDTO(CodeDTDAO dao);

    List<ConceptDTO> toConceptDTOList(List<CodeDTDAO> dao);

    default List<ConceptDTO> toConceptDTOList(CodeDTDAO codeDTDAO) {
        return Arrays.asList(this.toConceptDTO(codeDTDAO));
    }

    @Mappings({
            @Mapping(target = "targetNotation", source = "notation"),
            @Mapping(target = "targetName", source = "prefLabel"),
            @Mapping(target = "uri", source = "id"),
            @Mapping(target = "targetFrameworkURI", source = "inScheme.id")
    })
    CodeDTDAO toCodeDTDAO(ConceptDTO dao);

    default CodeDTDAO toCodeDTDAO(List<ConceptDTO> conceptDTOS) {
        return this.toCodeDTDAO(conceptDTOS.stream().findFirst().orElse(null));
    }

    List<CodeDTDAO> toCodeDTDAOList(List<ConceptDTO> dao);

    default TextDTDAO toTextDTDAO(LiteralMap map) {
        if(map == null || map.isEmpty()) {
            return null;
        }

        TextDTDAO textDTDAO = new TextDTDAO();
        textDTDAO.setContents(new ArrayList<>());

        for(Map.Entry<String, List<String>> entry : map.entrySet()) {
            for(String value : entry.getValue()) {
                textDTDAO.getContents().add(new ContentDTDAO(value, entry.getKey()));
            }
        }

        return textDTDAO;
    }

    @Mappings({
            @Mapping(target = "noteLiteral", source = "contents")
    })
    NoteDTO toNoteDTO(NoteDTDAO dao);

    Identifier toIdentifier(IdentifierDTDAO identifier);

    @Mappings({
            @Mapping(target = "spatial", source = "spatialId")
    })
    LegalIdentifier toLegalIdentifier(LegalIdentifierDTDAO identifier);

    @Mappings({
            @Mapping(target = "contentURL", source = "contentUrl")
    })
    WebResourceDTO webDocumentDCDAOToWebResourceDTO(WebDocumentDCDAO webDocumentDCDAO);

}
