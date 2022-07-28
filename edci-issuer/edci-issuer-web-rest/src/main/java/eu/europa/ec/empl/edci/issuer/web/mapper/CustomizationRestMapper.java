package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.customization.*;
import eu.europa.ec.empl.edci.issuer.web.model.customization.*;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CustomizationRestMapper {


    CustomizableEntityDTO toDTO(CustomizableEntityView customizableEntityView);

    @Mapping(target = "label", source = "labelKey", qualifiedByName = "doTranslate")
    CustomizableEntityView toVO(CustomizableEntityDTO customizableEntityDTO, @Context EDCIMessageService edciMessageService);

    Set<CustomizableEntityDTO> toDTO(Set<CustomizableEntityView> customizableEntityView);

    Set<CustomizableEntityView> toVO(Set<CustomizableEntityDTO> customizableEntityDTO, @Context EDCIMessageService edciMessageService);

    @Mapping(target = "customizableEntityViews", source = "customizableEntityDTOS")
    CustomizableSpecView toVO(CustomizableSpecDTO customizableSpecDTO, @Context EDCIMessageService edciMessageService);

    @Mapping(target = "customizableEntityDTOS", source = "customizableEntityViews")
    CustomizableSpecDTO toDTO(CustomizableSpecView customizableSpecView);

    CustomizableFieldDTO toDTO(CustomizableFieldView customizableFieldView);

    @Mapping(target = "label", source = "labelKey", qualifiedByName = "doTranslate")
    CustomizableFieldView toVO(CustomizableFieldDTO customizableFieldDTO, @Context EDCIMessageService edciMessageService);

    Set<CustomizableFieldDTO> toCFDTOSet(Set<CustomizableFieldView> customizableFieldView);

    Set<CustomizableFieldView> toCFViewSet(Set<CustomizableFieldDTO> customizableFieldDTO, @Context EDCIMessageService edciMessageServic);

    CustomizableRelationDTO toDTO(CustomizableRelationView customizableRelationView);

    default CustomizedRecipientDTO toDTO(CustomizedRecipientView customizedRecipientView) {
        if ( customizedRecipientView == null ) {
            return null;
        }

        CustomizedRecipientDTO customizedRecipientDTO = new CustomizedRecipientDTO();

        if (customizedRecipientView.getEntities() != null && !customizedRecipientView.getEntities().isEmpty()) {
            for(CustomizedEntityView entity : customizedRecipientView.getEntities()) {
                customizedRecipientDTO.getFields().addAll(toCFDTO(entity.getFields()));
                customizedRecipientDTO.getRelations().addAll(toCSRDTO(entity.getRelations()));
            }
        }

        return customizedRecipientDTO;
    }

    @Mapping(target = "label", source = "labelKey", qualifiedByName = "doTranslate")
    CustomizableRelationView toVO(CustomizableRelationDTO customizableRelationDTO, @Context EDCIMessageService edciMessageService);

    Set<CustomizableRelationDTO> toCREDTO(Set<CustomizableRelationView> customizableRelationView);

    Set<CustomizableRelationView> toCREView(Set<CustomizableRelationDTO> customizableRelationDTO, @Context EDCIMessageService edciMessageServic);

    CustomizedFieldDTO toDTO(CustomizedFieldView customizedFieldView);

    CustomizedFieldView toVO(CustomizedFieldDTO customizedFieldDTO);

    Set<CustomizedFieldDTO> toCFDTO(Set<CustomizedFieldView> customizedFieldView);

    Set<CustomizedFieldView> toCFView(Set<CustomizedFieldDTO> customizedFieldDTO);

    CustomizedRecipientsDTO toDTO(CustomizedRecipientsView customizedRecipientsView);

    CustomizedRecipientsView toVO(CustomizedRecipientsDTO customizedRecipientsDTO);

    CustomizedRecipientDTO toDTO(CustomizedEntityView customizedRecipientView);

    CustomizedEntityView toVO(CustomizedRecipientDTO customizedRecipientDTO);

    Set<CustomizedRecipientDTO> toCRDTO(Set<CustomizedEntityView> customizedRecipientView);

    Set<CustomizedEntityView> toCRView(Set<CustomizedRecipientDTO> customizedRecipientDTO);

    CustomizedRelationDTO toDTO(CustomizedRelationView customizedRelationView);

    CustomizedRelationView toVO(CustomizedRelationDTO customizedRelationDTO);

    Set<CustomizedRelationDTO> toCSRDTO(Set<CustomizedRelationView> customizedRelationView);

    Set<CustomizedRelationView> toCSRView(Set<CustomizedRelationDTO> customizedRelationDTO);

    CustomizableInstanceView toCIView(CustomizableInstanceDTO customizableInstanceDTO, @Context EDCIMessageService edciMessageService);

    Set<CustomizableInstanceView> toCIView(Set<CustomizableInstanceDTO> customizableInstanceDTOS, @Context EDCIMessageService edciMessageService);

    CustomizableInstanceDTO toDTO(CustomizableInstanceView customizableView);

    Set<CustomizableInstanceDTO> toView(Set<CustomizableInstanceView> customizableInstanceViews);

    CustomizableInstanceFieldView toCFView(CustomizableInstanceFieldDTO customizableFieldDTO, @Context EDCIMessageService edciMessageService);

    Set<CustomizableInstanceFieldView> toCFIView(Set<CustomizableInstanceFieldDTO> customizableFieldDTOS, @Context EDCIMessageService edciMessageService);

    CustomizableInstanceRelationView toCRView(CustomizableInstanceRelationDTO customizableInstanceRelationDTO, @Context EDCIMessageService edciMessageService);

    Set<CustomizableInstanceRelationView> toCRView(Set<CustomizableInstanceRelationDTO> customizableInstanceRelationDTO, @Context EDCIMessageService edciMessageService);

    @Mapping(target = "customizableInstanceViews", source = "customizableInstanceDTOS")
    CustomizableInstanceSpecView toVO(CustomizableInstanceSpecDTO customizableSpecDTO, @Context EDCIMessageService edciMessageService);

    @Mapping(target = "customizableInstanceDTOS", source = "customizableInstanceViews")
    CustomizableInstanceSpecDTO toDTO(CustomizableInstanceSpecView customizableInstanceSpecView);

    @Named("doTranslate")
    default String doTranslate(String key, @Context EDCIMessageService edciMessageService) {
        return edciMessageService.getMessage(key);
    }
}
