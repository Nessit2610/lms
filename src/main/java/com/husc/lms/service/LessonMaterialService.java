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
	
	
	public LessonMaterialResponse createMaterial(String lessonid, MultipartFile file) {
		Lesson lesson = lessonRepository.findById(lessonid).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		LessonMaterial lessonMaterial = LessonMaterial.builder()
				.lesson(lesson)
				.build();
		lessonMaterial = lessonMaterialRepository.save(lessonMaterial);
		String url = uploadFile(lessonMaterial.getId(), file);
		lessonMaterial.setPath(url);
		lessonMaterial = lessonMaterialRepository.save(lessonMaterial);
		
		LessonMaterialResponse ler = LessonMaterialResponse.builder()
				.path(url)
				.build();
		return ler;
	}
	
	public String uploadFile(String id, MultipartFile file) {
	    if (file.isEmpty()) {
	        throw new RuntimeException("File is empty");
	    }
	    
	    LessonMaterial lessonMaterial = lessonMaterialRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));  
	    String fileUrl = fileUploadFunction.apply(id, file);
	    lessonMaterial.setPath(fileUrl);
	    lessonMaterialRepository.save(lessonMaterial);
	    return fileUrl;
	}

	private final Function<String, String> fileExtension = filename ->
	        Optional.ofNullable(filename)
	                .filter(name -> name.contains("."))
	                .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
	                .orElse("");

	private final BiFunction<String, MultipartFile, String> fileUploadFunction = (id, file) -> {
	    String extension = fileExtension.apply(file.getOriginalFilename());
	    String filename = id + "_" + System.currentTimeMillis() + extension;

	    try {
	        // Tuỳ vào loại file (video hay không), chọn thư mục phù hợp
	        String folder = getFileFolder(extension);

	        // Xử lý lưu file vào thư mục đã chọn
	        Path fileStorageLocation = Paths.get(folder).toAbsolutePath().normalize();
	        Files.createDirectories(fileStorageLocation);

	        // Đường dẫn hoàn chỉnh lưu file
	        Path targetLocation = fileStorageLocation.resolve(filename);
	        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

	        // Trả về URL truy cập file
	        return ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/" + getFolderPath(extension) + "/")  // Dựa trên loại file (ảnh, video, file)
	                .path(filename)
	                .toUriString();
	    } catch (IOException e) {
	        throw new RuntimeException("Unable to upload file", e);
	    }
	};

	// Hàm xác định thư mục lưu trữ phù hợp (video, ảnh, hay file chung)
	private String getFileFolder(String extension) {
	    if (isVideo(extension)) {
	        return Constant.VIDEO_DIRECTORY;  // Video lưu vào thư mục "videos"
	    } else if (isImage(extension)) {
	        return Constant.PHOTO_DIRECTORY;  // Ảnh lưu vào thư mục "images"
	    } else {
	        return Constant.FILE_DIRECTORY;  // Các file khác sẽ lưu vào thư mục "files"
	    }
	}

	// Hàm xác định đường dẫn để truy cập file qua URL (dựa vào loại file)
	private String getFolderPath(String extension) {
	    if (isVideo(extension)) {
	        return "lessonmaterial";  // Video sẽ truy cập qua /videos/{filename}
	    } else if (isImage(extension)) {
	        return "lessonmaterial";  // Ảnh sẽ truy cập qua /images/{filename}
	    } else {
	        return "lessonmaterial";  // File sẽ truy cập qua /files/{filename}
	    }
	}

	// Kiểm tra nếu là video (các định dạng phổ biến như mp4, avi, mkv, ... )
	private boolean isVideo(String extension) {
	    String ext = extension.toLowerCase();
	    return ext.equals(".mp4") || ext.equals(".avi") || ext.equals(".mov") || ext.equals(".mkv");
	}

	// Kiểm tra nếu là ảnh (các định dạng phổ biến như jpg, png, gif, ...)
	private boolean isImage(String extension) {
	    String ext = extension.toLowerCase();
	    return ext.equals(".jpg") || ext.equals(".png") || ext.equals(".gif") || ext.equals(".jpeg");
	}
	
}
