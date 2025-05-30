package com.husc.lms.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.configuration.LimitedInputStream;
import com.husc.lms.constant.Constant;
import com.husc.lms.constant.TriFunction;
import com.husc.lms.dto.request.ChapterRequest;
import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.dto.update.ChapterUpdateRequest;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentLessonChapterProgress;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.ChapterMapper;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;

@Service
public class ChapterService {

	@Autowired
	private ChapterRepository chapterRepository;
	
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private ChapterMapper chapterMapper;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private StudentLessonChapterProgressService slcpService;
	
	@Autowired
	private StudentLessonChapterProgressRepository slcpRepository;
	
	public ChapterResponse createChapter(ChapterRequest request) {
		var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
		Lesson lesson = lessonRepository.findById(request.getLessonId()).get();
		if (chapterRepository.existsByLessonAndOrderAndDeletedDateIsNull(lesson, request.getOrder())) {
	        throw new AppException(ErrorCode.CHAPTER_ORDER_DUPLICATE);
	    }
		String extension = fileExtension.apply(request.getFile().getOriginalFilename());
		validateFileExtension(request.getType(), extension);
		Chapter chapter = Chapter.builder()
				.name(request.getName())
				.order(request.getOrder())
				.lesson(lesson)
				.createdBy(nameAccount)
				.createdDate(new Date())
				.build();
		chapter = chapterRepository.save(chapter);
		String chapterId = chapter.getId();
		uploadFile(chapterId, request.getFile(), request.getType());
		
		Course course = courseRepository.findById(lesson.getCourse().getId())
				.orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

		List<Student> studentsInCourse = course.getStudentCourses().stream()
				.map(studentCourse -> studentCourse.getStudent())
				.collect(Collectors.toList());
		// Tạo notification cho các sinh viên trong Course
		for (Student student : studentsInCourse) {
			Notification notification = Notification.builder()
					.account(student.getAccount())
					.description(
							"Giảng viên " + course.getTeacher().getFullName() + " vừa đăng 1 bài học mới trong chương " + lesson.getDescription())
					.type(NotificationType.JOIN_CLASS_PENDING.name())
					.createdAt(OffsetDateTime.now())
					.build();
			notificationRepository.save(notification);

			// Gửi thông báo qua WebSocket
			Map<String, Object> payload = new HashMap<>();
			payload.put("receivedAccount", student.getAccount().getUsername());
			payload.put("message",
					"Giảng viên " + course.getTeacher().getFullName() + " vừa đăng 1 bài học mới trong chương " + course.getName());
			payload.put("type", NotificationType.POST_CREATED.name());
			payload.put("courseId", course.getId());
			payload.put("lessonId", lesson.getId());
			payload.put("chapterId", chapter.getId());
			payload.put("createdAt", OffsetDateTime.now());

			notificationService.sendCustomWebSocketNotificationToUser(student.getAccount().getUsername(), payload);
		}
		
		return chapterMapper.toChapterResponse(chapter);
	}
	
	public ChapterResponse updateChapter(ChapterUpdateRequest request) {
	    var context = SecurityContextHolder.getContext();
	    String nameAccount = context.getAuthentication().getName();

	    Chapter chapter = chapterRepository.findById(request.getChapterId())
	        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

	    Lesson lesson = chapter.getLesson();

	    boolean isOrderExist = chapterRepository
	        .findByLessonAndOrderAndDeletedDateIsNull(lesson, request.getOrder())
	        .map(existing -> !existing.getId().equals(request.getChapterId())) 
	        .orElse(false);

	    if (isOrderExist) {
	        throw new AppException(ErrorCode.CHAPTER_ORDER_DUPLICATE); 
	    }

	    chapter.setName(request.getName());
	    chapter.setOrder(request.getOrder());
	    chapter.setType(request.getType());
	    chapter.setLastModifiedBy(nameAccount);
	    chapter.setLastModifiedDate(new Date());

	    chapter = chapterRepository.save(chapter);
	    uploadFile(request.getChapterId(), request.getFile(), request.getType());

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
            List<StudentLessonChapterProgress> slcpList = slcpRepository.findByChapter(chapter);
            if(slcpList != null && !slcpList.isEmpty()) {
            	slcpService.deleteChapterProgress(slcpList);
            }
            List<Chapter> remainingChapters = chapterRepository
                .findByLessonAndDeletedDateIsNullOrderByOrderAsc(chapter.getLesson());
            int newOrder = 1;
            for (Chapter c : remainingChapters) {
                c.setOrder(newOrder++);
            }
            chapterRepository.saveAll(remainingChapters);
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

        return "/lms/chapter/" + folder + "/" + filename;

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
