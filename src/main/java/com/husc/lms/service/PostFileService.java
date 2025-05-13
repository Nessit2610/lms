package com.husc.lms.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.configuration.LimitedInputStream;
import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.entity.Post;
import com.husc.lms.entity.PostFile;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.PostFileRepository;

@Service
public class PostFileService {
	
	@Autowired
	private PostFileRepository postFileRepository;

	public PostFile creatPostFile(Post post,MultipartFile file, String type) {
		if(file != null  && !file.isEmpty() && type != null) {
			String extension = fileExtension.apply(file.getOriginalFilename());
			validateFileExtension(type, extension);
		}
		PostFile postFile = PostFile.builder()
				.post(post)
				.build();
		postFile = postFileRepository.save(postFile);
		if(file != null  && !file.isEmpty() && type != null) {
			uploadFile(postFile.getId(), file, type);
		}
		return postFile;
	}

	public void deleteFile(PostFile postFile) {
		postFileRepository.delete(postFile);
		deletePhysicalFile(postFile.getFileUrl());
	}
	
	
	public String uploadFile(String id, MultipartFile file, String type) {
	    if (file == null || file.isEmpty()) {
	        throw new RuntimeException("File is empty");
	    }

	    PostFile postFile = postFileRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    String fileUrl = generalFileUploadFunction.apply(id, type.toLowerCase(), file);
	    postFile.setFileName(file.getOriginalFilename());
	    postFile.setFileType(type);
	    postFile.setFileUrl(fileUrl);
	    postFileRepository.save(postFile);

	    return fileUrl;
	}


	private final Function<String, String> fileExtension = filename ->
    Optional.ofNullable(filename)
            .filter(name -> name.contains("."))
            .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
            .orElse("");

	private String getFolderFromType(String type) {
	    return switch (type.toLowerCase()) {
	        case "image" -> "images";
	        case "video" -> "videos";
	        case "file"-> "files";
	        default -> throw new RuntimeException("Unsupported file type: " + type);
	    };
	}

	private final TriFunction<String, String, MultipartFile, String> generalFileUploadFunction = (id, type, file) -> {
    String extension = fileExtension.apply(file.getOriginalFilename());
    validateFileExtension(type, extension);
    
    String filename = id + extension;
    String folder = getFolderFromType(type);
    String baseDir = switch (folder) {
        case "images" -> Constant.PHOTO_DIRECTORY;
        case "videos" -> Constant.VIDEO_DIRECTORY;
        case "files" -> Constant.FILE_DIRECTORY;
        default -> throw new RuntimeException("Invalid folder: " + folder);
    };

    
    try {
        Path storagePath = Paths.get(baseDir).toAbsolutePath().normalize();
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        Path destination = storagePath.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return "/lms/postfile/" + folder + "/" + filename;

    } catch (IOException e) {
        throw new RuntimeException("Could not save file: " + filename, e);
    	}
	};
	
	private void deletePhysicalFile(String fileUrl) {
	    if (fileUrl == null || fileUrl.isEmpty()) return;

	    String[] parts = fileUrl.split("/");
	    if (parts.length < 4) return;

	    String folder = parts[3];      
	    String filename = parts[4];    

	    String baseDir = switch (folder) {
	        case "images" -> Constant.PHOTO_DIRECTORY;
	        case "videos" -> Constant.VIDEO_DIRECTORY;
	        case "files" -> Constant.FILE_DIRECTORY;
	        default -> throw new RuntimeException("Unsupported folder: " + folder);
	    };

	    try {
	        Path path = Paths.get(baseDir, filename);
	        Files.deleteIfExists(path);
	        System.out.println("Đã xóa file: " + path.toString());
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Không thể xóa file: " + filename, e);
	    }
	}
	
	public ResponseEntity<Resource> streamVideo(String filename, String rangeHeader) throws IOException {
        File videoFile = Paths.get(Constant.VIDEO_DIRECTORY + filename).toFile();
        long fileLength = videoFile.length();

        if (rangeHeader == null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(new FileSystemResource(videoFile));
        }

        long start, end;
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        start = Long.parseLong(ranges[0]);
        end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;

        long contentLength = end - start + 1;
        InputStream inputStream = new FileInputStream(videoFile);
        inputStream.skip(start);
        InputStreamResource inputStreamResource = new InputStreamResource(new LimitedInputStream(inputStream, contentLength));

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength)
                .body(inputStreamResource);
    }
	
	public ResponseEntity<byte[]> getFile(String filename) throws IOException {
        Path path = Paths.get(Constant.FILE_DIRECTORY + filename);
        byte[] data = Files.readAllBytes(path);

        String mimeType = Files.probeContentType(path); // Lấy content-type tự động

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(data);
    }
	
	private void validateFileExtension(String type, String extension) {
	    Set<String> imageExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif");
	    Set<String> videoExtensions = Set.of(".mp4", ".avi", ".mov");
	    Set<String> fileExtensions = Set.of(".pdf", ".doc", ".docx", ".txt");

	    switch (type.toLowerCase()) {
	        case "image" -> {
	            if (!imageExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
	            }
	        }
	        case "video" -> {
	            if (!videoExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_VIDEO_TYPE);
	            }
	        }
	        case "file" -> {
	            if (!fileExtensions.contains(extension)) {
	                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
	            }
	        }
	        default -> throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);
	    }
	}

}
