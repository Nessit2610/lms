package com.husc.lms.service;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.configuration.LimitedInputStream;
import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.dto.response.LessonMaterialResponse;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.LessonMaterial;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.LessonMaterialRepository;
import com.husc.lms.repository.LessonRepository;

@Service
public class LessonMaterialService {

	@Autowired
	private LessonMaterialRepository lessonMaterialRepository;
	
	
	@Autowired
	private LessonRepository lessonRepository;
	
	
	public LessonMaterialResponse createMaterial(String lessonid, MultipartFile file, String type) {
		String extension = fileExtension.apply(file.getOriginalFilename());
		validateFileExtension(type, extension);
		var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
		Lesson lesson = lessonRepository.findById(lessonid).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		LessonMaterial lessonMaterial = LessonMaterial.builder()
				.lesson(lesson)
				.createdBy(nameAccount)
				.createdDate(new Date())
				.build();
		lessonMaterial = lessonMaterialRepository.save(lessonMaterial);
		String url = uploadFile(lessonMaterial.getId(), file, type);
		LessonMaterialResponse ler = LessonMaterialResponse.builder()
				.path(url)
				.build();
		return ler;
	}
	
	public boolean deleteMaterial(String id) {
		var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
		LessonMaterial material = lessonMaterialRepository.findByIdAndDeletedDateIsNull(id);
		if(material != null) {
			material.setDeletedBy(nameAccount);
			material.setDeletedDate(new Date());
			lessonMaterialRepository.save(material);
			return true;
		}
		return false;
	}
	
	public void deleteMaterialByLesson(Lesson lesson) {
		List<LessonMaterial> lessonMaterials =lessonMaterialRepository.findByLessonAndDeletedDateIsNull(lesson);
		for(LessonMaterial l : lessonMaterials) {
			deleteMaterial(l.getId());
		}
	}
	
	
	public String uploadFile(String id, MultipartFile file, String type) {
	    if (file == null || file.isEmpty()) {
	        throw new RuntimeException("File is empty");
	    }

	    LessonMaterial lessonMaterial = lessonMaterialRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    String fileUrl = generalFileUploadFunction.apply(id, type.toLowerCase(), file);
	    lessonMaterial.setFileName(file.getOriginalFilename());
	    lessonMaterial.setPath(fileUrl);
	    lessonMaterialRepository.save(lessonMaterial);

	    return fileUrl;
	}


	private final Function<String, String> fileExtension = filename ->
    Optional.ofNullable(filename)
            .filter(name -> name.contains("."))
            .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
            .orElse("");

	private String getFolderFromType(String type) {
	    return switch (type.toLowerCase()) {
	        case "photo", "image" -> "images";
	        case "video" -> "videos";
	        case "file", "document" -> "files";
	        default -> throw new RuntimeException("Unsupported file type: " + type);
	    };
	}

	private final TriFunction<String, String, MultipartFile, String> generalFileUploadFunction = (id, type, file) -> {
    String extension = fileExtension.apply(file.getOriginalFilename());
    String filename = id + extension;

    String folder = getFolderFromType(type);
    String baseDir = switch (folder) {
        case "images" -> Constant.PHOTO_DIRECTORY;
        case "videos" -> Constant.VIDEO_DIRECTORY;
        case "files" -> Constant.FILE_DIRECTORY;
        default -> throw new RuntimeException("Invalid folder: " + folder);
    };

    validateFileExtension(type, extension);
    
    try {
        Path storagePath = Paths.get(baseDir).toAbsolutePath().normalize();
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        Path destination = storagePath.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return "/lms/lessonmaterial/" + folder + "/" + filename;

    } catch (IOException e) {
        throw new RuntimeException("Could not save file: " + filename, e);
    	}
	};

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
	    Set<String> fileExtensions = Set.of(".pdf", ".doc", ".docx", ".txt", ".pptx");

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
