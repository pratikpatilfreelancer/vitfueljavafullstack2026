package com.evms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

/**
 * Service for handling signature file uploads and storage.
 * <p>
 * Replaces the Multer-based file upload functionality from the Node.js backend.
 * Signature images are stored on the local filesystem under a configurable
 * upload directory (default: {@code ./uploads/signatures/}).
 * <p>
 * Files are saved with unique names using the pattern:
 * {@code sig_{timestamp}_{random}.{extension}} to prevent naming collisions.
 *
 * @author EVMS Team
 */
@Service
public class FileStorageService {

    /** Base directory where signature files are stored. */
    private final Path uploadDir;

    /** Base directory where proof document files are stored. */
    private final Path proofDir;

    /** Maximum allowed file size: 10 MB. */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /** Allowed MIME types for proof documents (images + PDF). */
    private static final Set<String> ALLOWED_PROOF_TYPES = Set.of(
            "image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp",
            "application/pdf"
    );

    /**
     * Constructs the FileStorageService and ensures the upload directories exist.
     *
     * @param uploadPath the upload directory path from application.properties
     */
    public FileStorageService(@Value("${file.upload-dir:./uploads/signatures}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        // Proof documents stored alongside signatures under uploads/proofs/
        this.proofDir = this.uploadDir.getParent().resolve("proofs").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.proofDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories: " + uploadPath, e);
        }
    }

    /**
     * Stores a multipart file (signature image) to the upload directory.
     * <p>
     * Validates that the file is an image (MIME type starts with "image/").
     * Generates a unique filename to prevent collisions.
     *
     * @param file the uploaded multipart file
     * @return the generated filename (not the full path)
     * @throws RuntimeException if the file type is invalid or storage fails
     */
    public String storeFile(MultipartFile file) {
        // Validate file size — max 10 MB
        validateFileSize(file);

        // Validate MIME type — only image files are allowed
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed for signatures.");
        }

        // Generate a unique filename: sig_<timestamp>_<random>.<ext>
        String originalFilename = file.getOriginalFilename();
        String extension = ".png"; // Default extension
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "sig_" + System.currentTimeMillis() + "_"
                + Math.round(Math.random() * 1_000_000) + extension;

        try {
            // Copy the file to the upload directory
            Path targetLocation = this.uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    /**
     * Stores a proof document file (image or PDF) to the proofs directory.
     * <p>
     * Validates that the file is an allowed type (image or PDF).
     * Generates a unique filename to prevent collisions.
     *
     * @param file the uploaded multipart file
     * @return the generated filename (not the full path)
     * @throws RuntimeException if the file type is invalid or storage fails
     */
    public String storeProofDocument(MultipartFile file) {
        // Validate file size — max 10 MB
        validateFileSize(file);

        // Validate MIME type — only image or PDF files are allowed
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_PROOF_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Only image files (PNG, JPEG, GIF, WebP) and PDF files are allowed as proof documents.");
        }

        // Generate a unique filename: proof_<timestamp>_<random>.<ext>
        String originalFilename = file.getOriginalFilename();
        String extension = ".png"; // Default extension
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "proof_" + System.currentTimeMillis() + "_"
                + Math.round(Math.random() * 1_000_000) + extension;

        try {
            Path targetLocation = this.proofDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store proof document: " + filename, e);
        }
    }

    /**
     * Deletes a signature file from the upload directory.
     * <p>
     * Used when a DRAFT voucher is deleted to clean up its signature file.
     * Silently ignores errors if the file doesn't exist.
     *
     * @param filename the name of the file to delete
     */
    public void deleteFile(String filename) {
        try {
            Path filePath = this.uploadDir.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Silently ignore — same behavior as the Node.js backend
        }
    }

    /**
     * Deletes a proof document file from the proofs directory.
     * <p>
     * Used when a DRAFT voucher is deleted or its proof is replaced.
     * Silently ignores errors if the file doesn't exist.
     *
     * @param filename the name of the proof file to delete
     */
    public void deleteProofDocument(String filename) {
        try {
            Path filePath = this.proofDir.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Silently ignore
        }
    }

    /**
     * Validates that the uploaded file does not exceed the maximum allowed size.
     *
     * @param file the uploaded multipart file
     * @throws RuntimeException if the file exceeds the size limit
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException(
                    String.format("File size (%.1f MB) exceeds the maximum allowed size of 10 MB.",
                            file.getSize() / (1024.0 * 1024.0)));
        }
    }
}
