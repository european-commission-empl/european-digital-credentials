package eu.europa.ec.empl.edci.issuer.web.mapper.spec;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.ElementCLDAO;
import eu.europa.ec.empl.edci.issuer.entity.controlledLists.LabelCLDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.ContentDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring")
public interface GenericEntitiyRestMapper {

    List<CodeDTView> toCodeViewList(List<ElementCLDAO> dao, @Context String locale);

    CodeDTView toCodeView(ElementCLDAO dao, @Context String locale);

    CodeDTView toCodeView(Code dao);

    CodeDTView toCodeView(CodeDTDAO dao);

    default TextDTView toTextDTDAOFromLabelList(List<LabelCLDAO> labelList) {

        if (labelList == null) {
            return null;
        }

        return new TextDTView() {{
            setContents(labelList.stream().map(l -> new ContentDTView(l.getName(), l.getLang())).collect(Collectors.toList()));
        }};

    }
    
}
