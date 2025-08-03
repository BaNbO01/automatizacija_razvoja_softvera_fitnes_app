package rs.ac.bg.fon.nst.fitnes.service.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import rs.ac.bg.fon.nst.fitnes.exception.FileUploadException;
import rs.ac.bg.fon.nst.fitnes.exception.ResourceNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")

@TestPropertySource(properties = "file.upload-dir=target/test-uploads")
class FileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

   
    private Path testUploadDir;

    @BeforeEach
    void setUp() throws IOException {
      
        this.testUploadDir = Paths.get("target/test-uploads");
      
        if (Files.exists(this.testUploadDir)) {
            Files.walk(this.testUploadDir)
                 .sorted((p1, p2) -> -p1.compareTo(p2)) 
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         System.err.println("Greška prilikom brisanja: " + path);
                     }
                 });
        }
    
        Files.createDirectories(this.testUploadDir);
    }

    @AfterEach
    void tearDown() throws IOException {
      
        Files.walk(this.testUploadDir)
             .sorted((p1, p2) -> -p1.compareTo(p2))
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (IOException e) {
                     System.err.println("Greška prilikom brisanja: " + path);
                 }
             });
       
        Files.deleteIfExists(this.testUploadDir);
    }

    @Test
    void testStoreFile_Success() throws IOException {
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "Hello, world!".getBytes()
        );
        String originalFileName = "testUser";

       
        String returnedPath = fileStorageService.storeFile(file, originalFileName);

       
        assertNotNull(returnedPath);
        assertTrue(returnedPath.startsWith(originalFileName + "/"));

       
        Path storedFile = testUploadDir.resolve(returnedPath);
        assertTrue(Files.exists(storedFile));
        assertEquals("Hello, world!", Files.readString(storedFile));
    }

    @Test
    void testStoreFile_PathTraversalAttack() {
      
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../dangerous.txt",
                "text/plain",
                "Hello, hacker!".getBytes()
        );
        String originalFileName = "testUser";

       
        assertThrows(FileUploadException.class, () -> {
            fileStorageService.storeFile(file, originalFileName);
        });
    }

    @Test
    void testLoadFileAsResource_Success() throws IOException {
       
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "dummy.txt",
                "text/plain",
                "Test content".getBytes()
        );
        String originalFileName = "testUser";
        String storedPath = fileStorageService.storeFile(file, originalFileName);

       
        Resource resource = fileStorageService.loadFileAsResource(storedPath);

      
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
       
        String fileName = storedPath.substring(storedPath.lastIndexOf("/") + 1);
        assertEquals(fileName, resource.getFilename());
    }

    @Test
    void testLoadFileAsResource_NotFound() {
       
        assertThrows(ResourceNotFoundException.class, () -> {
            fileStorageService.loadFileAsResource("non_existent_file.jpg");
        });
    }

    @Test
    void testDeleteFile_Success() throws IOException {
      
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "to_be_deleted.txt",
                "text/plain",
                "Delete me".getBytes()
        );
        String originalFileName = "testUser";
        String storedPath = fileStorageService.storeFile(file, originalFileName);

       
        boolean deleted = fileStorageService.deleteFile(storedPath);

       
        assertTrue(deleted);
        assertFalse(Files.exists(testUploadDir.resolve(storedPath)));
    }

    @Test
    void testDeleteFile_NotFound() {
       
        boolean deleted = fileStorageService.deleteFile("non_existent_file.txt");

       
        assertFalse(deleted);
    }
}
