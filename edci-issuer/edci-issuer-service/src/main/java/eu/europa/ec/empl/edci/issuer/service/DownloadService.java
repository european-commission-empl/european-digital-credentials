package eu.europa.ec.empl.edci.issuer.service;


import eu.europa.ec.empl.edci.annotation.Resize;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.datamodel.model.base.DownloadableAsset;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.proxy.DataLoaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class DownloadService {

    private static final Logger logger = LogManager.getLogger(DownloadService.class);

    @Autowired
    public Validator validator;

    @Autowired
    public EDCIMessageService edciMessageService;

    @Autowired
    public ReflectiveUtil reflectiveUtil;

    @Autowired
    public ResourcesUtil resourcesUtil;

    @Autowired
    public IssuerConfigService issuerConfigService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    private DataLoaderUtil dataLoaderUtil;

    @Autowired
    public FileUtil fileUtil;

    public <T> List<String> downloadAssets(T rootAsset) {
        List<String> errors = new ArrayList<String>();
        Map<Method, Set<Object>> assetsMethods = reflectiveUtil.getUniqueInnerMethodsOfType(DownloadableAsset.class, rootAsset);
        assetsMethods.keySet().forEach(downloadableAssetMethod -> {
            assetsMethods.get(downloadableAssetMethod).forEach(object -> {
                Resize resize = downloadableAssetMethod.getAnnotation(Resize.class);
                if (Collection.class.isAssignableFrom(downloadableAssetMethod.getReturnType())) {
                    List<DownloadableAsset> assets = resourcesUtil.getCollectionFromMethod(object, DownloadableAsset.class, downloadableAssetMethod);
                    assets.forEach(asset -> {
                        String error = downloadAsset(asset, resize);
                        if (validator.notEmpty(error)) {
                            errors.add(error);
                        }
                    });
                } else {
                    String error = downloadAsset(resourcesUtil.getObjectFromMethod(object, DownloadableAsset.class, downloadableAssetMethod), resize);
                    if (validator.notEmpty(error)) {
                        errors.add(error);
                    }
                }
            });
        });
//        reflectiveUtil.getUniqueInnerObjectsOfType(DownloadableAsset.class, rootAsset, null).forEach(downloadableAsset -> {
//            String error = downloadAsset(downloadableAsset, null);
//            if (validator.notEmpty(error)) errors.add(error);
//        });
        return errors;
    }

    private String downloadAsset(DownloadableAsset downloadableAsset, Annotation annotation) {
        String error = null;
        if (downloadableAsset != null && validator.notEmpty(downloadableAsset.getContentUrl())) {
            try {
                downloadFile(downloadableAsset, annotation);
            } catch (EDCIException ex) {
                error = edciMessageService.getMessage(ex.getMessageKey(), downloadableAsset.getContentUrl());
            } catch (Exception e) {
                error = edciMessageService.getMessage(EDCIIssuerMessageKeys.ERROR_DOWNLOADABLE_ASSET, downloadableAsset.getContentUrl());
            }
        }
        return error;
    }

    private void downloadFile(DownloadableAsset downloadableAsset, Annotation annotation) throws IOException {
        dataLoaderUtil.setProxyConfig(issuerConfigService.proxyEDCIConfig());
        Code encoding = controlledListCommonsService.searchConceptByUri(ControlledList.ENCODING.getUrl(), ControlledListConcept.ENCODING_BASE64.getUrl(), EDCIConfig.Defaults.DEFAULT_LOCALE);
        dataLoaderUtil.downloadAsset(downloadableAsset, (fileType) -> fileUtil.getFileType(fileType), imageUtil, encoding, annotation);

    }
}
