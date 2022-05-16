package eu.europa.ec.empl.edci.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

@Service
public class EDCIFileService {


    public File getOrCreateFile(String path) {
        return new File(this.getNormalizedFilePath(path));
    }

    public File getOrCreateFile(File folder, String file) {
        return new File(folder, this.getNormalizedFilePath(file));
    }

    private String getNormalizedFilePath(String path) {
        return Paths.get(path).normalize().toString();
    }
}
