package eu.europa.ec.empl.edci.wallet.service.liquibase;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.context.SpringApplicationContext;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.liquibase.EDCIAbstractCustomTask;
import eu.europa.ec.empl.edci.model.external.EDCISignatureReports;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.ExternalServicesUtil;
import eu.europa.ec.empl.edci.wallet.entity.ConversionLockDAO;
import eu.europa.ec.empl.edci.wallet.entity.ConversionLogDAO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import eu.europa.ec.empl.edci.wallet.repository.ConversionLockRepository;
import eu.europa.ec.empl.edci.wallet.repository.ConversionLogRepository;
import eu.europa.ec.empl.edci.wallet.repository.CredentialRepository;
import eu.europa.ec.empl.edci.wallet.service.CredentialService;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.WalletService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialStorageUtil;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MigrateToFileSystemTask {

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private EDCIMessageService edciMessageService;

    private static final Logger logger = LogManager.getLogger(MigrateToFileSystemTask.class);

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 365, initialDelay = 1000 * 60) //fixed: 24 hours after the previous process has ended
    protected void executeChangeAsync() {

        boolean activeProcess = walletConfigService.getBoolean("migrate.credentials.active", true);

        if (!activeProcess) {
            logger.info("Credential migration process is inactive");
            return;
        }

        logger.info("Starting migration process");

        BeanFactory beanFactory = SpringApplicationContext.getBeanFactory();
        ConversionLockRepository conversionLockRepository = beanFactory.getBean(ConversionLockRepository.class);
        ExternalServicesUtil externalServicesUtil = beanFactory.getBean(ExternalServicesUtil.class);
        WalletService walletService = beanFactory.getBean(WalletService.class);
        CredentialStorageUtil credentialStorageUtil = beanFactory.getBean(CredentialStorageUtil.class);
        ConversionLogRepository conversionLogRepository = beanFactory.getBean(ConversionLogRepository.class);
        CredentialRepository credentialRepository = beanFactory.getBean(CredentialRepository.class);

        try {
            long miliseconds = (long)(Math.random() * 15000);
            Thread.sleep(miliseconds);
            logger.info("Credential migration process - Waiting " + miliseconds + " before starting the process");
        } catch (InterruptedException e) {
            logger.error("Credential migration process - Error waiting 0 to 15 seconds to avoid an execution in parallel");
        }

        List<ConversionLockDAO> lockList = conversionLockRepository.findAll();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, - walletConfigService.getInteger("migrate.credentials.lock.hours", 1));
        for (ConversionLockDAO lock : lockList) {
            if (lock.getExecutionDate().before(calendar.getTime())) {
                conversionLockRepository.delete(lock);
            } else {
                logger.info("Credential migration process has been started by another server in this database");
                return;
            }
        }

        ConversionLockDAO newLock = new ConversionLockDAO();
        newLock.setExecutionDate(new Date());
        conversionLockRepository.saveAndFlush(newLock);

        try {

            List<Long> resultSet = credentialRepository.fetchCredentialsToMigrate();
            for (Long credPk : resultSet) {

                CredentialDAO cred = credentialRepository.getOne(credPk);
                WalletDAO wallet = cred.getWallet();

                String credId = cred.getUuid();
                byte[] credentialBytes = cred.getCredential();
                String userEmail = wallet.getUserEmail();
                String userId = wallet.getUserId();
                String folder = wallet.getFolder();
                String file = cred.getFile();
                Long walletPk = cred.getWallet().getPk();

                if (!StringUtils.isEmpty(file)) {
                    continue;
                }

                ConversionLogDAO conversionLog = new ConversionLogDAO(walletPk, userId, userEmail, credPk, credId);

                try {
                    byte[] jsonLdCredential = externalServicesUtil.convertCredential(credentialBytes);

                    EDCISignatureReports reports = null;
                    try {
                        reports = credentialService.validateCredential(credentialBytes, ContentType.APPLICATION_JSON);
                    } catch (Exception e) {
                        conversionLog.setErrorCode(ErrorCode.CONVERSION_EXTERNAL_ISSUE.getCode());
                    }

                    String walletFolder = !StringUtils.isEmpty(folder) ? folder : walletService.createWalletFolderIfNotCreated(wallet);

                    String fileStored = credentialStorageUtil.storeCredentialIntoFilesystem(walletFolder, jsonLdCredential);

                    if (!StringUtils.isEmpty(fileStored)) {
                        String info = "OK";
                        if (reports != null) {
                            cred.setSignatureExpiryDate(reports.getExpiryDate());
                        } else {
                            info = "Warning: Signature Expiry Date not saved";
                        }
                        cred.setSigned(false);
                        cred.setFile(fileStored);
                        credentialRepository.save(cred);
                        conversionLog.setInfo(info);
                    } else {
                        conversionLog.setErrorCode(ErrorCode.UNDEFINED.getCode());
                        conversionLog.setInfo("Unstored file");
                    }

                } catch (EDCIException e) {
                    conversionLog.setErrorCode(e.getCode().getCode());
                    String errorDesc = edciMessageService.getMessage(e.getMessage());
                    if (e.getDescription() != null) {
                        errorDesc += " More info: " + e.getDescription();
                    }
                    if (cred.getCreateDate().before(new SimpleDateFormat("dd-MM-yyyy").parse("25-10-2021"))) {
                        errorDesc = "Credential generated before 25-10-2021 (Official EDC launch) - " + errorDesc;
                    }
                    if (errorDesc != null && errorDesc.length() > 4000) {
                        errorDesc = errorDesc.substring(0,3999);
                    }
                    conversionLog.setInfo(errorDesc);
                    logger.error("Credential's migration process log - Error converting credential: " + credPk);

                } catch (EDCIRestException e) {
                    conversionLog.setErrorCode(e.getCode().getCode());
                    String errorDesc = edciMessageService.getMessage(e.getMessage());
                    if (e.getDescription() != null) {
                        errorDesc += " More info: " + e.getDescription();
                    }
                    if (cred.getCreateDate().before(new SimpleDateFormat("dd-MM-yyyy").parse("25-10-2021"))) {
                        errorDesc = "Credential generated before 25-10-2021 (Official EDC launch) - " + errorDesc;
                    }
                    if (errorDesc != null && errorDesc.length() > 4000) {
                        errorDesc = errorDesc.substring(0,3999);
                    }
                    conversionLog.setInfo(errorDesc);
                    logger.error("Credential's migration process log - Error converting credential: " + credPk);

                } catch (Exception e) {
                    conversionLog.setErrorCode(ErrorCode.UNDEFINED.getCode());
                    String errorDesc = edciMessageService.getMessage(e.getMessage());
                    if (errorDesc != null && errorDesc.length() > 4000) {
                        errorDesc = errorDesc.substring(0,3999);
                    }
                    conversionLog.setInfo(errorDesc);
                    logger.error("Credential's migration process log - Error converting credential: " + credPk);
                }
                try {
                    conversionLog.setEndDate(new Date());
                    conversionLogRepository.saveAndFlush(conversionLog);
                    logger.debug("Credential successfully converted: " + credPk);
                } catch (Exception e) {
                    logger.error("Credential's migration process log - Error saving conversionLog for credential: " + credPk);
                }
            }
        } catch (Exception e) {
            logger.error("Credential's migration process log - Error converting credentials", e);
        } finally {
            logger.info("Removing migration process lock: " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(newLock.getExecutionDate()));
            conversionLockRepository.delete(newLock);
        }

        logger.info("Finishing migration process");

    }

    protected Logger getLogger() {
        return logger;
    }

}
