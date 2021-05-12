package eu.europa.ec.empl.edci.security.service.base;

import eu.europa.ec.empl.edci.security.model.dto.UserDetailsDTO;

public interface IEDCIUserService {

    public UserDetailsDTO getUserInfo();

    public String getUserId();

}
