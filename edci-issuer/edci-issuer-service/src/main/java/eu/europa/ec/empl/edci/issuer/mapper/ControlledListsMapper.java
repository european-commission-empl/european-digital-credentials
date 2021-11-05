package eu.europa.ec.empl.edci.issuer.mapper;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Content;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.ElementCLDAO;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.LabelCLDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.ContentDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.issuer.utils.ecso.EscoElementPayload;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFDescription;
import org.apache.log4j.Logger;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ControlledListsMapper {

    public static final org.apache.log4j.Logger logger = Logger.getLogger(ControlledListsMapper.class);

    List<ElementCLDAO> toCLList(List<RDFDescription> rdf);

    default ElementCLDAO toCL(RDFDescription desc) throws Exception {

        ElementCLDAO elem = new ElementCLDAO();

        try {
            elem.setUri(desc.getUri());
            if (desc.getTargetName() != null) {
                elem.setTargetName(desc.getTargetName().stream().map(label -> new LabelCLDAO(label.getName(), label.getLang()))
                        .collect(Collectors.toList()));
            }
            if (desc.getTargetDescription() != null) {
                elem.setTargetDescription(desc.getTargetDescription().stream().map(label -> new LabelCLDAO(label.getName(), label.getLang()))
                        .collect(Collectors.toList()));
            }
            elem.setTargetFrameworkURI(desc.getTargetFrameworkURI().getValue());
            elem.setTargetNotation(desc.getTargetNotation());
            if (desc.getTargetFramework() != null) {
                elem.setTargetFramework(desc.getTargetFramework().stream().map(label -> new LabelCLDAO(label.getName(), label.getLang()))
                        .collect(Collectors.toList()));
            }
            if (desc.getExternal() != null) {
                elem.setExternal(desc.getExternal().getValue());
            }

            elem.setLastUpdated(new Date());
            elem.setDeprecatedSince(desc.getDeprecated() != null && desc.getDeprecated() ? new Date() : null);
        } catch (Exception e) {
            elem.setUri(desc.getUri() + "MAPPING_ERROR");
            elem.setTargetFrameworkURI(desc.getTargetFrameworkURI() != null ? desc.getTargetFrameworkURI().getValue() : "NO_TARGETFRAMEWORK_URI");
            logger.error(e);
        }
        return elem;
    }

    /* **************
     *  CL to Code  *
     ****************/

    CodeDTDAO toCodeDAO(ElementCLDAO dao, @Context Collection<String> retrieveLangs);

    List<CodeDTDAO> toCodeDAOList(List<ElementCLDAO> dao, @Context Collection<String> retrieveLangs);

    Code toCodeDTO(ElementCLDAO dao, @Context Collection<String> retrieveLangs);

    Code toCodeDTOESCO(EscoElementPayload dao, @Context Collection<String> retrieveLangs);

    CodeDTDAO toCodeDAOESCO(EscoElementPayload dao, @Context Collection<String> retrieveLangs);

    Code toCode(CodeDTDAO dao, @Context Collection<String> retrieveLangs);

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

    default List<LabelCLDAO> toLabelList(Text label, @Context Collection<String> retrieveLangs) {

        if (label == null) {
            return null;
        }

        return label.getContents().stream().filter(locale -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(locale.getLanguage())))
                .map(c -> new LabelCLDAO(c.getContent(), c.getLanguage())).collect(Collectors.toList());

    }

    default Text toTextFromLabelList(List<LabelCLDAO> labelList, @Context Collection<String> retrieveLangs) {

        if (labelList == null) {
            return null;
        }

        Text text = new Text();
        text.setContents(labelList.stream().filter(locale -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(locale.getLang())))
                .map(l -> new Content(l.getName(), l.getLang())).collect(Collectors.toList()));
        if (text.getContents() == null || text.getContents().isEmpty()) {
            text = null;
        }

        return text;
    }

    default Text toTextFromMap(Map<String, String> labelList, @Context Collection<String> retrieveLangs) {

        if (labelList == null) {
            return null;
        }

        Text text = new Text();
        text.setContents(labelList.keySet().stream().filter(k -> (retrieveLangs == null || retrieveLangs.isEmpty() || retrieveLangs.contains(k)))
                .map(k -> new Content(labelList.get(k), k)).collect(Collectors.toList()));
        if (text.getContents() == null || text.getContents().isEmpty()) {
            text = null;
        }
        return text;
    }


}
