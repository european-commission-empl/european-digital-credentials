package eu.europa.ec.empl.edci.model.external.qdr;

import java.util.ArrayList;
import java.util.List;

public class QDRSearchResponseDTO {

    private List<QDRAccreditationLiteDTO> accreditations = new ArrayList<>();
    private QDRPaginationInfoDTO paginationInfos = new QDRPaginationInfoDTO();


    public List<QDRAccreditationLiteDTO> getAccreditations() {
        return accreditations;
    }

    public void setAccreditations(List<QDRAccreditationLiteDTO> accreditations) {
        this.accreditations = accreditations;
    }

    public QDRPaginationInfoDTO getPaginationInfos() {
        return paginationInfos;
    }

    public void setPaginationInfos(QDRPaginationInfoDTO paginationInfos) {
        this.paginationInfos = paginationInfos;
    }

    public Integer getTotalMatchingCount() {
        return this.getPaginationInfos().getTotalMatchingCount();
    }
    
}
