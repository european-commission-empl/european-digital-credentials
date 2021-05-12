package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.AssessmentDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.issuer.common.model.AssessmentsListIssueDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import org.mapstruct.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, AgentOrganizationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssessmentMapper {

    @Mappings({
            @Mapping(target = "specifiedBy.gradingScheme", source = "specifiedBy.gradingSchemes")
    })
    AssessmentDTO toDTO(AssessmentSpecDAO assessmentSpecDAO);

    @Mappings({
            @Mapping(target = "specifiedBy.gradingSchemes", source = "specifiedBy.gradingScheme")
    })
    AssessmentSpecDAO toDAO(AssessmentDTO assessmentDTO);

    default AssessmentsListIssueDTO toListIssueDTO(Set<AssessmentSpecDAO> assessmentSpecDAOS) {
        AssessmentsListIssueDTO assmView = new AssessmentsListIssueDTO();
        assmView.getAssessments().putAll(assessmentSpecDAOS.stream().collect(Collectors.toMap(k -> k.getPk(), v -> v.getDefaultTitle())));
        return assmView;
    }

    default IdentifierDTDAO identifierToIdentifierDTDAO(Identifier identifier) {
        if (identifier == null) {
            return null;
        }

        IdentifierDTDAO identifierDTDAO = new IdentifierDTDAO();

        identifierDTDAO.setContent(identifier.getContent());
        identifierDTDAO.setIdentifierSchemeId(identifier.getIdentifierSchemeId());
        identifierDTDAO.setIdentifierSchemeAgencyName(identifier.getIdentifierSchemeAgencyName());
        if (identifier.getIssuedDate() != null) {
            identifierDTDAO.setIssuedDate(identifier.getIssuedDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }
        List<String> list = identifier.getIdentifierType();
        if (list != null) {
            identifierDTDAO.setIdentifierType(new HashSet<>(list));
        } else {
            identifierDTDAO.setIdentifierType(null);
        }

        return identifierDTDAO;
    }

    default Identifier identifierDTDAOToIdentifier(IdentifierDTDAO identifierDTDAO) {
        if (identifierDTDAO == null) {
            return null;
        }

        Identifier identifier = new Identifier();

        identifier.setContent(identifierDTDAO.getContent());
        identifier.setIdentifierSchemeId(identifierDTDAO.getIdentifierSchemeId());
        identifier.setIdentifierSchemeAgencyName(identifierDTDAO.getIdentifierSchemeAgencyName());
        if (identifierDTDAO.getIssuedDate() != null) {
            identifier.setIssuedDate(java.util.Date.from(identifierDTDAO.getIssuedDate().atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
        }
        Set<String> list = identifierDTDAO.getIdentifierType();
        if (list != null) {
            identifier.setIdentifierType(new ArrayList<String>(list));
        } else {
            identifier.setIdentifierType(null);
        }

        return identifier;
    }

    @BeforeMapping()
    default void checkEmptySpec(AssessmentSpecDAO source, @MappingTarget AssessmentDTO target) {
        if (source != null && source.getSpecifiedBy() != null) {
            if (source.getSpecifiedBy().isEmpty()) {
                source.setSpecifiedBy(null);
            }
        }
    }
}
