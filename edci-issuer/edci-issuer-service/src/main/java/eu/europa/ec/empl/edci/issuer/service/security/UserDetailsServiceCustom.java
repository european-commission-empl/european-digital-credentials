package eu.europa.ec.empl.edci.issuer.service.security;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import eu.cec.digit.ecas.client.jaas.DetailedUser;
//import eu.europa.ec.empl.edci.issuer.common.ecas.UserHolder;
//import eu.europa.ec.empl.edci.issuer.common.security.UserContext;
//import eu.europa.ec.empl.edci.issuer.common.dto.security.RoleDTO;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    //    private final static Logger logger = LoggerFactory.getLogger(UserDetailsServiceCustom.class);
    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserDetailsServiceCustom.class);

//    @Inject
//    private UserHolder userHolder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        try {

//            DetailedUser detailedUser = userHolder.getDetailedUser();
//            Long perId = userHolder.extractEcasEmployeeNumber(detailedUser);
//
//            UserContext userContext = new UserContext(username);
//            userContext.setEmail(detailedUser.getEmail());
//            userContext.setDomain(detailedUser.getDomain());
//            userContext.setPerId(perId);
//            userContext.setDetailedUser(detailedUser);
//            userContext.setFirstName(detailedUser.getFirstName());
//            userContext.setLastName(detailedUser.getLastName());
//
//            userContext.setRoleList(new LinkedList<RoleDTO>());
//
//            if (existsClass("weblogic.security.Security")) {
//                Subject subject = (Subject) Class.forName("weblogic.security.Security").getMethod("getCurrentSubject", null).invoke(null, null);
//                Set<Principal> principals = subject.getPrincipals();
//                for (Principal principal : principals) {
//                    if (Class.forName("weblogic.security.spi.WLSGroup").isInstance(principal)) {
//                        RoleDTO role = new RoleDTO();
//                        role.setName(principal.getName());
//                        userContext.getRoleList().add(role);
//                        logger.info("---------------------------------------REGISTERED ROLE : " + principal.getName());
//                    }
//                }
//            }

//            userHolder.setUser(userContext);

//            return userContext;
            return null;

        } catch (Exception ex) {
            if (ex instanceof UsernameNotFoundException) {
                throw (UsernameNotFoundException) ex;
            } else
                throw new UsernameNotFoundException("Cannot retrieve the userDetails", ex);
        }
    }

    public boolean existsClass(String claszzName) {
        boolean exists = true;
        try {
            Class.forName(claszzName, false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            exists = false;
        }
        return exists;
    }


    public UserDetails getUserContext() {
//        return userHolder.getUser();
        return null;
    }


}
