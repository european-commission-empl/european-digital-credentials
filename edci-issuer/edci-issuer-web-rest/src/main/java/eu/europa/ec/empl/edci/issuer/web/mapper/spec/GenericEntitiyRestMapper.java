package eu.europa.ec.empl.edci.issuer.web.mapper.spec;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.LabelCLDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.ContentDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import eu.europa.ec.empl.edci.mapper.commons.StringUriMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring",
        uses = {StringUriMapping.class})
public interface GenericEntitiyRestMapper {

    CodeDTView toCodeView(CodeDTDAO dao);

    @Mappings({
            @Mapping(source = "id", target = "uri"),
            @Mapping(source = "inScheme.id", target = "targetFrameworkURI"),
            @Mapping(source = "prefLabel", target = "targetName"),
            @Mapping(source = "notation", target = "targetNotation")
    })
    CodeDTView toCodeView(ConceptDTO dao);

    default TextDTView toTextDTView(LiteralMap literalMap) {
        if(literalMap == null)
            return null;
        TextDTView textDTView = new TextDTView();
        List<ContentDTView> contents = literalMap.entrySet()
                .stream()
                .filter(stringListEntry -> stringListEntry.getValue() != null && !stringListEntry.getValue().isEmpty())
                .map(stringListEntry ->
                        new ContentDTView(stringListEntry.getValue().stream().findFirst().orElse(null), stringListEntry.getKey())
                ).collect(Collectors.toList());
        textDTView.setContents(contents);
        return textDTView;
    }

    default TextDTView toTextDTDAOFromLabelList(List<LabelCLDAO> labelList) {

        if (labelList == null) {
            return null;
        }

        return new TextDTView() {{
            setContents(labelList.stream().map(l -> new ContentDTView(l.getName(), l.getLang())).collect(Collectors.toList()));
        }};

    }
}
