package com.husc.lms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.dto.request.ChapterRequest;
import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.LessonMaterial;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.ChapterMapper;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.LessonRepository;

@Service
public class ChapterService {

	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private ChapterMapper chapterMapper;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	public ChapterResponse createChapter(String lessonId, String name, int order, MultipartFile file, String type) {
		var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
		Lesson lesson = lessonRepository.findById(lessonId).get();
		Chapter chapter = Chapter.builder()
				.name(name)
				.order(order)
				.lesson(lesson)
				.createdBy(nameAccount)
				.createdDate(new Date())
				.build();
		chapter = chapterRepository.save(chapter);
		String chapterId = chapter.getId();
		uploadFile(chapterId, file, type);
		return chapterMapper.toChapterResponse(chapter);
	}
	
	public ChapterResponse updateChapter(String chapterId, String name, int order, MultipartFile file, String type) {
		var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
		Chapter chapter = chapterRepository.findById(chapterId).get();
			chapter.setName(name);
			chapter.setOrder(order);
			chapter.setType(type);
			chapter.setLastModifiedBy(nameAccount);
			chapter.setLastModifiedDate(new Date());
		chapter = chapterRepository.save(chapter);
		uploadFile(chapterId, file, type);
		return chapterMapper.toChapterResponse(chapter);
	}

    public boolean deleteChapter(String id) {
    	var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
        Chapter chapter = chapterRepository.findByIdAndDeletedDateIsNull(id);
        if (chapter != null) {
            chapter.setDeletedBy(nameAccount);
            chapter.setDeletedDate(new Date());
            chapterRepository.save(chapter);
            return true;
        }
        return false;
    }
	
    public void deleteChapterByLesson(Lesson lesson) {
    	List<Chapter> listChapter = chapterRepository.findByLessonAndDeletedDateIsNull(lesson);
    	for(Chapter c : listChapter) {
    		deleteChapter(c.getId());
    	}
    }
	
	public String uploadFile(String id, MultipartFile file, String type) {
	    if (file == null || file.isEmpty()) {
	        throw new RuntimeException("File is empty");
	    }

	    Chapter chapter = chapterRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    String fileUrl = generalFileUploadFunction.apply(id, type.toLowerCase(), file);
	    chapter.setPath(fileUrl);
	    chapter.setType(type);
	    chapterRepository.save(chapter);

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

        return "/lms/chapter/" + folder + "/" + filename;

    } catch (IOException e) {
        throw new RuntimeException("Could not save file: " + filename, e);
    	}
	};
}
