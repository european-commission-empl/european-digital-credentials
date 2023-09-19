package eu.europa.ec.empl.edci.wallet.service.liquibase;

import eu.europa.ec.empl.edci.context.SpringApplicationContext;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.EmailLockDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.repository.EmailLockRepository;
import eu.europa.ec.empl.edci.wallet.repository.WalletRepository;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProcessDuplicatedWalletEmailsTask {

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CredentialStorageUtil credentialStorageUtil;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CredentialRepository credentialRepository;

    private static final Logger logger = LogManager.getLogger(ProcessDuplicatedWalletEmailsTask.class);

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 365, initialDelay = 10000)
    protected void executeChangeAsync() {

        boolean activeProcess = walletConfigService.getBoolean("convert.emails.lowerCase", false);

        if (!activeProcess) {
            logger.info("TEMP_WALLET_EMAIL_REVISION - Convert lower emails process is inactive");
            return;
        }

        logger.info("TEMP_WALLET_EMAIL_REVISION - Starting convert lower emails  process");

        BeanFactory beanFactory = SpringApplicationContext.getBeanFactory();
        EmailLockRepository emailLockRepository = beanFactory.getBean(EmailLockRepository.class);

        try {
            long miliseconds = (long)(Math.random() * 15000);
            Thread.sleep(miliseconds);
            logger.error("TEMP_WALLET_EMAIL_REVISION - Convert lower emails process - Waiting " + miliseconds + " before starting the process");
        } catch (InterruptedException e) {
            logger.error("TEMP_WALLET_EMAIL_REVISION - Convert lower emails process - Error waiting 0 to 15 seconds to avoid an execution in parallel");
        }


        List<EmailLockDAO> lockList = emailLockRepository.findAll();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, - walletConfigService.getInteger("convert.emails.lowerCase.lock.hours", 1));
        for (EmailLockDAO lock : lockList) {
            if (lock.getExecutionDate().before(calendar.getTime())) {
                emailLockRepository.delete(lock);
            } else {
                logger.error("TEMP_WALLET_EMAIL_REVISION - Convert lower emails process has been started by another server in this database");
                return;
            }
        }

        EmailLockDAO newLock = new EmailLockDAO();
        newLock.setExecutionDate(new Date());
        emailLockRepository.saveAndFlush(newLock);

        try {
            List<Object[]> emailList = walletRepository.listDuplicatedEmails();
            List<String> lowerCasePermEmails = emailList.stream().map(obj -> obj[1].toString().toLowerCase()).collect(Collectors.toList());
            emailList.removeIf(email -> Collections.frequency(lowerCasePermEmails, email[1].toString().toLowerCase()) > 1);

            logger.error("TEMP_WALLET_EMAIL_REVISION - List of duplicated emails: " + emailList.size());

            for(Object[] tuple : emailList) {
                String tempEmail = (String) tuple[0];
                String permEmail = (String) tuple[1];

                WalletDAO tempWallet = walletRepository.fetchByUserEmailCaseSensitive(tempEmail);
                WalletDAO permWallet = walletRepository.fetchByUserEmailCaseSensitive(permEmail);

                try {

                    List<CredentialDAO> originalCreds = new ArrayList<>();
                    for(CredentialDAO cred : tempWallet.getCredentialDAOList()) {

                        CredentialDAO credentialDAO = new CredentialDAO();
                        credentialDAO.setFile(cred.getFile());
                        originalCreds.add(credentialDAO);

                        boolean ispresent = permWallet.getCredentialDAOList() != null && !permWallet.getCredentialDAOList().isEmpty() &&
                                permWallet.getCredentialDAOList().stream().filter(credential -> credential.getUuid().equalsIgnoreCase(cred.getUuid())).count() > 0;

                        if(!ispresent) {
                            byte[] credentialFile = credentialStorageUtil.getCredentialFromFileSystem(cred);

                            if(credentialFile != null) {
                                this.moveCredential(cred, permWallet, credentialFile);
                            }
                        }

                    }

                    credentialStorageUtil.deleteWalletStorage(tempWallet.getFolder(), originalCreds);
                    walletRepository.deleteById(tempWallet.getPk());
                } catch (Exception e) {
                    logger.error("TEMP_WALLET_EMAIL_REVISION - Error removing temporary wallet " + tempWallet.getWalletAddress(), e);
                }

            }

            List<Object[]> finalEmails = walletRepository.listDuplicatedEmails();
            List<Object[]> finalPermanentEmails = walletRepository.listDuplicatedEmails();
            List<String> lowerCaseFinalPermEmails = finalEmails.stream()
                    .map(obj -> obj[1].toString().toLowerCase()).collect(Collectors.toList());
            finalEmails.removeIf(email -> Collections.frequency(lowerCaseFinalPermEmails, email[1].toString().toLowerCase()) > 1);
            finalPermanentEmails.removeAll(finalEmails);

            logger.error("TEMP_WALLET_EMAIL_REVISION - List of remaining duplicated emails: " + finalEmails.size());
            logger.error("TEMP_WALLET_EMAIL_REVISION - List of remaining permanent duplicated emails: " + finalPermanentEmails.stream()
                    .map(obj -> obj[1].toString().toLowerCase()).collect(Collectors.toSet()).size());

        } catch (Exception e) {
            logger.error("TEMP_WALLET_EMAIL_REVISION - Convert lower emails process log - Error converting", e);
        } finally {
            logger.error("TEMP_WALLET_EMAIL_REVISION - Removing convert lower emails process lock: " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(newLock.getExecutionDate()));
            emailLockRepository.delete(newLock);
        }

        logger.error("TEMP_WALLET_EMAIL_REVISION - Finishing convert lower emails process");

    }

    @Transactional
    private void moveCredential(CredentialDAO cred, WalletDAO permWallet, byte[] credentialFile) {
        String walletFolder = !StringUtils.isEmpty(permWallet.getFolder()) ? permWallet.getFolder() : walletService.createWalletFolderIfNotCreated(permWallet);

        cred.setFile(credentialStorageUtil.storeCredentialIntoFilesystem(walletFolder, credentialFile));
        cred.setWallet(permWallet);
        credentialRepository.save(cred);
    }

    protected Logger getLogger() {
        return logger;
    }

}
