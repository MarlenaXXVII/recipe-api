package nl.novi.recipeapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ImageService {

    private final Path fileStoragePath;

    public ImageService(@Value("${my.upload_location}") String uploadLocation) throws IOException {
        fileStoragePath = Paths.get(uploadLocation).toAbsolutePath().normalize();
        Files.createDirectories(fileStoragePath);
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Please select an image.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(BAD_REQUEST, "Image must be smaller than 5 MB.");
        }

        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/webp");
        String contentType = file.getContentType();

        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new ResponseStatusException(BAD_REQUEST, "Only JPG, PNG and WebP images are allowed.");
        }

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new ResponseStatusException(BAD_REQUEST, "Invalid image type.");
        };

        String fileName = UUID.randomUUID() + extension;
        Path targetPath = fileStoragePath.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Could not save image.", e);
        }
    }

    public Resource downloadFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(NOT_FOUND, "Image not found.");
        }

        Path filePath = fileStoragePath.resolve(fileName).normalize();

        if (!filePath.startsWith(fileStoragePath)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid image path.");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            throw new ResponseStatusException(NOT_FOUND, "Image not found.");
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Could not read image.", e);
        }
    }
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        Path filePath = fileStoragePath.resolve(fileName).normalize();

        if (!filePath.startsWith(fileStoragePath)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid image path.");
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Could not delete image.", e);
        }
    }
}