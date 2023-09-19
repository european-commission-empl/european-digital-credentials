package eu.europa.ec.empl.edci.dss.util;

import eu.europa.ec.empl.edci.dss.model.signature.DSSTimestamp;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class DSSSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(DSSSignatureUtils.class);

    private DSSSignatureUtils() {
    }

    public static DSSDocument toDSSDocument(File file) {
        try {
            if ((file != null) && file.length() > 0) {
                DSSDocument document = new InMemoryDocument(Files.readAllBytes(file.toPath()), file.getName());
                return document;
            }
        } catch (IOException e) {
            logger.error("Cannot read  file : " + e.getMessage(), e);
        }
        return null;
    }

    public static DSSTimestamp fromTimestampToken(TimestampToken token) {
        DSSTimestamp dto = new DSSTimestamp();
        dto.setBase64Timestamp(Utils.toBase64(token.getEncoded()));
        dto.setCanonicalizationMethod(token.getCanonicalizationMethod());
        dto.setType(token.getTimeStampType());
        return dto;
    }

    public static TimestampToken toTimestampToken(DSSTimestamp dto) {
        try {
            TimestampToken token = new TimestampToken(Utils.fromBase64(dto.getBase64Timestamp()), dto.getType());
            token.setCanonicalizationMethod(dto.getCanonicalizationMethod());
            return token;
        } catch (Exception e) {
            throw new DSSException("Unable to retrieve the timestamp", e);
        }
    }

}
