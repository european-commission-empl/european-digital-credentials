package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningActivityDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningActivitySpecificationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.PeriodOfTimeDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningActSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.PeriodOfTimeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningActivitySpecDAO;
import eu.europa.ec.empl.edci.util.Validator;
import org.mapstruct.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, BaseDAOMapper.class, AgentOrganizationMapper.class, LearningAchievementMapper.class})
public interface LearningActivityMapper {

    public static final Validator validator = new Validator();

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "type", ignore = true)
    })
    LearningActivityDTO toDTO(LearningActivitySpecDAO learningActivitySpecDAO);

    @Mappings({
            @Mapping(target = "contactHour", source = "contactHours")
    })
    LearningActivitySpecificationDTO toDTO(LearningActSpecificationDCDAO learningActSpecificationDCDAO);

    List<LearningActivityDTO> toDTOList(List<LearningActivitySpecDAO> learningActivitySpecDAO);

    PeriodOfTimeDTO toPeriodOfTimeDTO(PeriodOfTimeDTDAO dto);

    @BeforeMapping()
    default void checkEmptySpec(LearningActivitySpecDAO source, @MappingTarget LearningActivityDTO target) {
        if (source != null && source.getSpecifiedBy() != null) {
            if (source.getSpecifiedBy().isEmpty()) {
                source.setSpecifiedBy(null);
            }
        }
    }

    @AfterMapping()
    default void setDates(LearningActivityDTO source, @MappingTarget LearningActivitySpecDAO target) {
        if (source != null && target != null && source.getTemporal() != null && !source.getTemporal().isEmpty()) {
            LocalDate startDate = validator.getValueNullSafe(() -> source.getTemporal().stream().findFirst().orElse(null).getStartDate().toLocalDate());
            LocalDate endDate = validator.getValueNullSafe(() -> source.getTemporal().stream().findFirst().orElse(null).getEndDate().toLocalDate());

            target.setStartedAtTime(startDate != null ? Date.valueOf(startDate) : null);
            target.setEndedAtTime(endDate != null ? Date.valueOf(endDate) : null);

        }
    }

    @AfterMapping()
    default void setDates(LearningActivitySpecDAO source, @MappingTarget LearningActivityDTO target) {
        if (source != null && target != null && (source.getStartedAtTime() != null || source.getEndedAtTime() != null)) {
            PeriodOfTimeDTO periodOfTimeDTO = new PeriodOfTimeDTO();
            periodOfTimeDTO.setStartDate(source.getStartedAtTime() != null ? ZonedDateTime.ofInstant(source.getStartedAtTime().toInstant(), ZoneId.of("UTC")) : null);
            periodOfTimeDTO.setEndDate(source.getEndedAtTime() != null ? ZonedDateTime.ofInstant(source.getEndedAtTime().toInstant(), ZoneId.of("UTC")) : null);

            target.getTemporal().add(periodOfTimeDTO);
        }
    }
}
