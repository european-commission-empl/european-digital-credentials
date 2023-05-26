package eu.europa.ec.empl.edci.dss.service.messages;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

@Service
public class FileService {

    public File getOrCreateFile(String path) {
        return new File(this.getNormalizedFilePath(path));
    }

    private String getNormalizedFilePath(String path) {
        return Paths.get(path).normalize().toString();
    }
}
