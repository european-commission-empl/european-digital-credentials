package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.MediaObjectDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.BaseDAOMapper;
import eu.europa.ec.empl.edci.issuer.repository.OrganizationSpecRepository;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class OrganizationSpecService implements CrudService<OrganizationSpecDAO> {
    private static final Logger logger = LogManager.getLogger(OrganizationSpecService.class);

    @Autowired
    private EDCIUserService edciUserService;

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private BaseDAOMapper baseDAOMapper;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private OrganizationSpecRepository organizationSpecRepository;

    public OrganizationSpecRepository getRepository() {
        return organizationSpecRepository;
    }

    public <T> OrganizationSpecDAO clone(Long oid, IRestMapper<OrganizationSpecDAO, T, ?> organizationSpecRestMapper) {

        OrganizationSpecDAO organizationSpecDAO = find(oid);
        if (organizationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        OrganizationSpecDAO organizationSpecCloneDAO = organizationSpecRestMapper.toDAO(organizationSpecRestMapper.toVO(organizationSpecDAO));
        organizationSpecCloneDAO.setPk(null);
        organizationSpecCloneDAO.setAuditDAO(null);
        organizationSpecCloneDAO.setLabel(this.generateDuplicatedLabel(organizationSpecCloneDAO.getLabel()));

        //Relations
        organizationSpecCloneDAO.setSubOrganizationOf(organizationSpecDAO.getSubOrganizationOf());
        organizationSpecCloneDAO.setAccreditation(new HashSet<>(organizationSpecDAO.getAccreditation()));

        return save(organizationSpecCloneDAO);

    }

    public OrganizationSpecDAO save(OrganizationSpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopLine(objectDAO, (a) -> a.getSubOrganizationOf(), "Parent Organization"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }

    public void addLogo(Long oid, MultipartFile file) {

        OrganizationSpecDAO organizationSpecDAO = find(oid);
        if (organizationSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Organization with oid [" + oid + "] not found");
        }

        try {


            Base64.Encoder base64Encoder = Base64.getMimeEncoder();

            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            byte[] imageContent = imageUtil.resizeImage(file.getBytes(), extension, ImageUtil.LOGO_HEIGHT, ImageUtil.LOGO_WIDTH);
            ConceptDTO fileType = fileUtil.getFileType(extension);

            MediaObjectDTDAO mediaObject = new MediaObjectDTDAO();
            mediaObject.setContent(imageContent);
            mediaObject.setContentType(baseDAOMapper.toCodeDTDAO(fileType));
            mediaObject.setContentEncoding(baseDAOMapper.toCodeDTDAO(imageUtil.getBase64Encoding()));

            organizationSpecDAO.setLogo(mediaObject);

            save(organizationSpecDAO);

        } catch (IOException e) {
            throw new EDCIException(e);
        }
    }

}