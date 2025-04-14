package com.husc.lms.service;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
		Lesson lesson = lessonRepository.findById(lessonid).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		LessonMaterial lessonMaterial = LessonMaterial.builder()
				.lesson(lesson)
				.build();
		lessonMaterial = lessonMaterialRepository.save(lessonMaterial);
		String url = uploadFile(lessonMaterial.getId(), file, type);
		LessonMaterialResponse ler = LessonMaterialResponse.builder()
				.path(url)
				.build();
		return ler;
	}
	
	public String uploadFile(String id, MultipartFile file, String type) {
	    if (file == null || file.isEmpty()) {
	        throw new RuntimeException("File is empty");
	    }

	    LessonMaterial lessonMaterial = lessonMaterialRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    String fileUrl = generalFileUploadFunction.apply(id, type.toLowerCase(), file);
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

    

}
