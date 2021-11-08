package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.MediaObjectDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.VariousObjectsMapper;
import eu.europa.ec.empl.edci.issuer.repository.DiplomaSpecRepository;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
public class DiplomaSpecService implements CrudService<DiplomaSpecDAO> {
    private static final Logger logger = Logger.getLogger(DiplomaSpecService.class);

    @Autowired
    private EDCIUserService edciUserService;

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private VariousObjectsMapper variousObjectsMapper;

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

        diplomaSpecCloneDAO.setDefaultTitle(generateTitleDuplicated(diplomaSpecCloneDAO.getDefaultTitle()));

        return save(diplomaSpecCloneDAO);

    }

    public void addBackground(Long oid, MultipartFile file) {

        DiplomaSpecDAO diplomaSpecDAO = find(oid);
        if (diplomaSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Diploma with oid [" + oid + "] not found");
        }

        try {

            Base64.Encoder base64Encoder = Base64.getMimeEncoder();
//            String base64EncoderImg = base64Encoder.encode(file.getBytes());

            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            byte[] imageContent = imageUtil.resizeImage(file.getBytes(), extension, ImageUtil.BACKGROUND_HEIGHT, ImageUtil.BACKGROUND_WIDTH);
            Code fileType = fileUtil.getFileType(extension);

            MediaObjectDTDAO mediaObject = new MediaObjectDTDAO();
            mediaObject.setContent(imageContent);
            mediaObject.setContentType(variousObjectsMapper.getCodeDTDAO(fileType));
            mediaObject.setContentEncoding(variousObjectsMapper.getCodeDTDAO(imageUtil.getBase64Encoding()));

            diplomaSpecDAO.setBackground(mediaObject);

            save(diplomaSpecDAO);

        } catch (IOException e) {
            throw new EDCIException(e);
        }
    }

}