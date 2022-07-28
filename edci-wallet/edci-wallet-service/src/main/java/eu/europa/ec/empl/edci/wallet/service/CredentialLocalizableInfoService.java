package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import eu.europa.ec.empl.edci.wallet.repository.CredentialLocalizableInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CredentialLocalizableInfoService implements WalletCrudService<CredentialLocalizableInfoDAO> {

    @Autowired
    private CredentialLocalizableInfoRepository credentialLocalizableInfoRepository;

    @Override
    public JpaRepository getRepository() {
        return credentialLocalizableInfoRepository;
    }
}
