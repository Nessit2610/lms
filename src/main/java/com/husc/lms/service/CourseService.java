package com.husc.lms.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import com.husc.lms.dto.response.CourseOfTeacherResponse;
import com.husc.lms.dto.response.CourseResponse;
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
import com.husc.lms.repository.TeacherRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private CourseMapperImplCustom courseMapperImpl;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private LessonService lessonService;
	
	public CourseResponse createCourse(CourseRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsername(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		Course course = courseMapper.toCourse(request);
				course.setTeacher(teacher);
				course.setCreatedBy(name);
				course.setCreatedDate(new Date());
				
		course = courseRepository.save(course);			
		return courseMapper.toCourseResponse(course);
		
	}
	
	public CourseResponse getCourseById(String id) {
		Course course = courseRepository.findByIdAndDeletedDateIsNull(id);
		return courseMapper.toCourseResponse(course);
	}
	
	public CourseResponse updateCourse(CourseUpdateRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Course course = courseRepository.findById(request.getIdCourse()).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		course = courseMapper.toCourse(request);
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
	
	public List<CourseResponse> getAllPublicCourse(){
		List<Course> courses = courseRepository.findByStatusAndDeletedDateIsNull(StatusCourse.PUBLIC.name());
		return courses.stream().map(courseMapperImpl::toFilteredCourseResponse).toList();
	}
	
	public List<CourseOfTeacherResponse> getCourseOfTeacher(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsername(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		List<Course> courses = courseRepository.findByTeacherAndDeletedDateIsNull(teacher);
		
		return courses.stream().map(courseMapper::toCourseOfTeacherResponse).toList();
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
