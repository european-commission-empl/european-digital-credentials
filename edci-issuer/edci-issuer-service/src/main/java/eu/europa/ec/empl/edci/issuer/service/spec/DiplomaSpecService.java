package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.MediaObjectDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.BaseDAOMapper;
import eu.europa.ec.empl.edci.issuer.repository.DiplomaSpecRepository;
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

@Service
@Transactional
public class DiplomaSpecService implements CrudService<DiplomaSpecDAO> {
    private static final Logger logger = LogManager.getLogger(DiplomaSpecService.class);

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
    private DiplomaSpecRepository diplomaSpecRepository;

    public DiplomaSpecRepository getRepository() {
        return diplomaSpecRepository;
    }

    public <T> DiplomaSpecDAO clone(Long oid, IRestMapper<DiplomaSpecDAO, T, ?> diplomaSpecRestMapper) {

        DiplomaSpecDAO diplomaSpecDAO = find(oid);
        if (diplomaSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Diploma with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        DiplomaSpecDAO diplomaSpecCloneDAO = diplomaSpecRestMapper.toDAO(diplomaSpecRestMapper.toVO(diplomaSpecDAO));
        diplomaSpecCloneDAO.setPk(null);
        diplomaSpecCloneDAO.setAuditDAO(null);
        diplomaSpecCloneDAO.setLabel(this.generateDuplicatedLabel(diplomaSpecCloneDAO.getLabel()));

        return save(diplomaSpecCloneDAO);

    }

    public void addBackground(Long oid, MultipartFile file) {

        DiplomaSpecDAO diplomaSpecDAO = find(oid);
        if (diplomaSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Diploma with oid [" + oid + "] not found");
        }

        try {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            byte[] imageContent = imageUtil.resizeImage(file.getBytes(), extension, ImageUtil.BACKGROUND_HEIGHT, ImageUtil.BACKGROUND_WIDTH);
            ConceptDTO fileType = fileUtil.getFileType(extension);

            MediaObjectDTDAO mediaObject = new MediaObjectDTDAO();
            mediaObject.setContent(imageContent);
            mediaObject.setContentType(baseDAOMapper.toCodeDTDAO(fileType));
            mediaObject.setContentEncoding(baseDAOMapper.toCodeDTDAO(imageUtil.getBase64Encoding()));

            diplomaSpecDAO.setBackground(mediaObject);

            save(diplomaSpecDAO);

        } catch (IOException e) {
            throw new EDCIException(e);
        }
    }

}