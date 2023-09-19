package eu.europa.ec.empl.edci.security.service;

import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import eu.europa.ec.empl.edci.security.model.dto.UserDetailsDTO;
import eu.europa.ec.empl.edci.security.model.mapper.UserMapper;
import eu.europa.ec.empl.edci.security.service.base.IEDCIUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EDCIUserService implements IEDCIUserService {

    @Autowired
    private EDCISecurityContextHolder edciUserHolder;

    @Autowired
    private EDCIAuthenticationService edciAuthenticationService;

    @Autowired
    private UserMapper userMapper;

    public EDCISecurityContextHolder getEdciUserHolder() {
        return edciUserHolder;
    }

    public void setEdciUserHolder(EDCISecurityContextHolder edciUserHolder) {
        this.edciUserHolder = edciUserHolder;
    }

    public UserMapper getUserMapper() {
        return userMapper;
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public EDCIAuthenticationService getEdciAuthenticationService() {
        return edciAuthenticationService;
    }

    public void setEdciAuthenticationService(EDCIAuthenticationService edciAuthenticationService) {
        this.edciAuthenticationService = edciAuthenticationService;
    }

    public String getUserId() {
        return this.getEdciUserHolder().getSub();
    }

    @Override
    public UserDetailsDTO getUserInfo() {
        if (this.getEdciAuthenticationService().isAuthenticated()) {
            UserDetailsDTO userDetailsDTO = this.getUserMapper().toDTO(this.getEdciUserHolder().getOIDCUserInfo());
            userDetailsDTO.setAuthenticated(this.getEdciAuthenticationService().isAuthenticated());
            return userDetailsDTO;
        } else {
            return new UserDetailsDTO(false);
        }
    }

}
