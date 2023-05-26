package eu.europa.ec.empl.edci.issuer.mapper;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.LabelCLDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.ContentDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoElementPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ControlledListsMapper {

    public static final Logger logger = LogManager.getLogger(ControlledListsMapper.class);

    @Mappings(
            {
                    @Mapping(target = "uri", source = "dao.uri"),
                    @Mapping(target = "targetFrameworkURI", source = "targetFramework"),
                    @Mapping(target = "targetName", source = "dao.targetName"),
                    @Mapping(target = "targetNotation", source = "dao.targetNotation")
            }
    )
    CodeDTDAO toCodeDAOESCO(EscoElementPayload dao, String targetFramework, @Context Collection<String> retrieveLangs);

    @Mappings(
            {
                    @Mapping(target = "id", source = "dao.uri"),
                    @Mapping(target = "inScheme.id", source = "targetFramework"),
                    @Mapping(target = "prefLabel", source = "dao.targetName"),
                    @Mapping(target = "notation", source = "dao.targetNotation")
            }
    )
    ConceptDTO toConceptDTOESCO(EscoElementPayload dao, String targetFramework, @Context Collection<String> retrieveLangs);

    default URI toString(String string) {
        return URI.create(string);
    }


    default TextDTDAO toTextDTDAOFromLabelList(List<LabelCLDAO> labelList, @Context Collection<String> retrieveLangs) {

        if (labelList == null) {
            return null;
        }

        TextDTDAO textDTDAO = new TextDTDAO();
        textDTDAO.setContents(labelList.stream().filter(locale -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(locale.getLang())))
                .map(l -> new ContentDTDAO(l.getName(), l.getLang())).collect(Collectors.toList()));
        if (textDTDAO.getContents() == null || textDTDAO.getContents().isEmpty()) {
            textDTDAO = null;
        }
        return textDTDAO;
    }

    default TextDTDAO toTextDTDAOFromMap(Map<String, String> labelList, @Context Collection<String> retrieveLangs) {

        if (labelList == null) {
            return null;
        }

        TextDTDAO textDTDAO = new TextDTDAO();
        textDTDAO.setContents(labelList.keySet().stream().filter(k -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(k)))
                .map(k -> new ContentDTDAO(labelList.get(k), k)).collect(Collectors.toList()));
        if (textDTDAO.getContents() == null || textDTDAO.getContents().isEmpty()) {
            textDTDAO = null;
        }
        return textDTDAO;
    }

    default List<LabelCLDAO> toLabelList(LiteralMap label, @Context Collection<String> retrieveLangs) {

        if (label == null) {
            return null;
        }

        return label.entrySet().stream()
                .filter(stringListEntry -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(stringListEntry.getKey())))
                .map(stringListEntry -> new LabelCLDAO(stringListEntry.getKey(), stringListEntry.getValue().stream().findFirst().orElse(EDCIConstants.StringPool.STRING_EMPTY)))
                .collect(Collectors.toList());
    }

    default LiteralMap toLiteralMapFromLabelList(List<LabelCLDAO> labelList, @Context Collection<String> retrieveLangs) {

        if (labelList == null) {
            return null;
        }

        LiteralMap text = (LiteralMap) labelList.stream().filter(locale -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(locale.getLang())))
                .collect(Collectors.toMap(label -> label.getLang(), label -> Arrays.asList(label.getName())));

        if (text == null || text.isEmpty()) {
            text = null;
        }

        return text;
    }

    default LiteralMap toLiteralMapFromMap(Map<String, String> labelList, @Context Collection<String> retrieveLangs) {

        if (labelList == null) {
            return null;
        }

        if (retrieveLangs == null || retrieveLangs.isEmpty()) {
            retrieveLangs = Arrays.asList(DataModelConstants.Defaults.DEFAULT_LOCALE);
        }

        Collection<String> filterLangs = retrieveLangs;

        LiteralMap text = new LiteralMap(labelList.entrySet().stream().filter(entry -> filterLangs.contains(entry.getKey()))
                .collect(Collectors.toMap(item -> item.getKey(), item -> Arrays.asList(item.getValue()))));

        if (text == null || text.isEmpty()) {
            text = null;
        }
        return text;
    }


}
