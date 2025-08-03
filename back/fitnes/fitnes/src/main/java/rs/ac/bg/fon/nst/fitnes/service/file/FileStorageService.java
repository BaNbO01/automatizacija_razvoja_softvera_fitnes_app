/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service.file;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import rs.ac.bg.fon.nst.fitnes.exception.FileUploadException;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

   
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation); 
        } catch (Exception ex) {
            throw new FileUploadException("Nije moguće kreirati direktorijum za upload fajlova.", ex);
        }
    }

  
    public String storeFile(MultipartFile file, String originalFileName) {
      
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
      
            if (fileName.contains("..")) {
                throw new FileUploadException("Naziv fajla sadrži nevalidnu putanju: " + fileName);
            }

         
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

          
            String sanitizedDirName = originalFileName.replaceAll("[^a-zA-Z0-9_-]", "_");
            Path targetDirectory = this.fileStorageLocation.resolve(sanitizedDirName);
            Files.createDirectories(targetDirectory); // Kreira poddirektorijum ako ne postoji

            Path targetLocation = targetDirectory.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

           
            return sanitizedDirName + "/" + uniqueFileName;

        } catch (IOException ex) {
            throw new FileUploadException("Nije moguće sačuvati fajl " + fileName + ". Molimo pokušajte ponovo!", ex);
        }
    }

  
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Fajl nije pronađen " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Fajl nije pronađen " + fileName, ex);
        }
    }

 
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Greška prilikom brisanja fajla " + fileName + ": " + ex.getMessage());
            return false;
        }
    }
}
