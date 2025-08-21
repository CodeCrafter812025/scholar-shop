package net.holosen.app.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // Try multiple possible locations
            String[] possibleLocations = {
                "src/main/resources/static/images/",
                "app/src/main/resources/static/images/", 
                "frontend/assets/images/",
                "images/"
            };
            
            // First try to find jpg version if webp is requested
            String jpgFilename = filename;
            if (filename.toLowerCase().endsWith(".webp")) {
                jpgFilename = filename.replace(".webp", ".jpg");
            }
            
            for (String location : possibleLocations) {
                try {
                    // Try original filename first
                    Path filePath = Paths.get(location).resolve(filename);
                    Resource resource = new UrlResource(filePath.toUri());
                    
                    if (resource.exists() && resource.isReadable()) {
                        String contentType = getContentType(filename);
                        return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))
                                .body(resource);
                    }
                    
                    // If not found and it's webp, try jpg version
                    if (!filename.equals(jpgFilename)) {
                        Path jpgPath = Paths.get(location).resolve(jpgFilename);
                        Resource jpgResource = new UrlResource(jpgPath.toUri());
                        
                        if (jpgResource.exists() && jpgResource.isReadable()) {
                            String contentType = getContentType(jpgFilename);
                            return ResponseEntity.ok()
                                    .contentType(MediaType.parseMediaType(contentType))
                                    .body(jpgResource);
                        }
                    }
                } catch (Exception e) {
                    // Continue to next location
                }
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private String getContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "image/jpeg"; // default
    }
}