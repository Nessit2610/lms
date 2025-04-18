package com.husc.lms.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.update.CourseUpdateRequest;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Teacher;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.StatusCourse;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.CourseMapperImpl;
import com.husc.lms.mapperImpl.CourseMapperImplCustom;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private CourseMapperImplCustom courseMapperImplCustom;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private LessonService lessonService;
	
	@Autowired
	private StudentCourseRepository studentCourseRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	public CourseResponse createCourse(CourseRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsername(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		
		String status = switch (request.getStatus()) {
        case "PRIVATE" -> StatusCourse.PRIVATE.name();
        case "PUBLIC" -> StatusCourse.PUBLIC.name();
        case "REQUEST" -> StatusCourse.REQUEST.name();
        default -> throw new AppException(ErrorCode.CODE_ERROR);
		};
		
		Course course = courseMapper.toCourse(request);
				course.setTeacher(teacher);
				course.setCreatedBy(name);
				course.setStatus(status);
				course.setCreatedDate(new Date());	
		course = courseRepository.save(course);		
		
		return courseMapper.toCourseResponse(course);
		
	}
	
	public CourseResponse getCourseById(String id) {
		Course course = courseRepository.findByIdAndDeletedDateIsNull(id);
		return courseMapperImplCustom.toFilteredCourseResponse(course);
	}
	
	public CourseResponse updateCourse(CourseUpdateRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Course course = courseRepository.findById(request.getIdCourse()).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		course.setName(request.getName());
		course.setDescription(request.getDescription());
		course.setEndDate(request.getEndDate());
		course.setMajor(request.getMajor());
		course.setStatus(request.getStatus());
		course.setLearningDurationType(request.getLearningDurationType());
		course.setLastModifiedBy(name);
		course.setLastModifiedDate(new Date());
		course = courseRepository.save(course);
		return courseMapper.toCourseResponse(course);
	}
	
	public boolean deleteCourse(String id) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Course course = courseRepository.findByIdAndDeletedDateIsNull(id);
		if(course != null) {
			lessonService.deleteLessonByCourse(course);
			course.setDeletedBy(name);
			course.setDeletedDate(new Date());
			courseRepository.save(course);
			return true;
		}
		return false;
	}
	
	public List<CourseViewResponse> getAllPublicCourse(){
		List<Course> courses = courseRepository.findByStatusInAndDeletedDateIsNull(Arrays.asList(StatusCourse.PUBLIC.name(), StatusCourse.REQUEST.name()));

		List<CourseViewResponse> courseResponses = new ArrayList<CourseViewResponse>();
		for(Course c : courses) {
			CourseViewResponse cr = courseMapper.toCourseViewResponse(c);
			cr.setStudentCount(studentCourseRepository.countStudentsByCourse(c));
			cr.setLessonCount(lessonRepository.countLessonsByCourse(c));
			courseResponses.add(cr);
		}
		return courseResponses;
	}
	
	public List<CourseViewResponse> getCourseOfTeacher(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsername(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		List<Course> courses = courseRepository.findByTeacherAndDeletedDateIsNull(teacher);
		List<CourseViewResponse> courseResponses = new ArrayList<CourseViewResponse>();
		for(Course c : courses) {
			CourseViewResponse cr = courseMapper.toCourseViewResponse(c);
			cr.setStudentCount(studentCourseRepository.countStudentsByCourse(c));
			cr.setLessonCount(lessonRepository.countLessonsByCourse(c));
			courseResponses.add(cr);
		}
		return courseResponses;
	}
	
	public String uploadPhoto(String id, MultipartFile file) {
		Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		String photoUrl = photoFunction.apply(id, file);
		course.setImage(photoUrl);
		courseRepository.save(course);
		return photoUrl;
	}
	
	private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");
	
	private final BiFunction<String, MultipartFile, String> photoFunction = (id,image)->{
		String filename  =  id + fileExtension.apply(image.getOriginalFilename());
		try {
			Path fileStorageLocation = Paths.get(Constant.PHOTO_DIRECTORY).toAbsolutePath().normalize();
			if(!Files.exists(fileStorageLocation)) {
				Files.createDirectories(fileStorageLocation);
			}
			Files.copy(image.getInputStream(), fileStorageLocation.resolve(id + fileExtension.apply(image.getOriginalFilename())),StandardCopyOption.REPLACE_EXISTING);
			return "/lms/course/image/" + filename;
		} catch (Exception e) {
			throw new RuntimeException("Unable to save image");
		}
	};
		
}
