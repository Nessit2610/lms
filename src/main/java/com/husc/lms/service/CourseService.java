package com.husc.lms.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.update.CourseUpdateRequest;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Major;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentCourse;
import com.husc.lms.entity.Teacher;
import com.husc.lms.enums.CourseLearningDurationType;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.FeeStatus;
import com.husc.lms.enums.StatusCourse;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapperImpl.CourseMapperImplCustom;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.MajorRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentRepository;
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
	private StudentRepository studentRepository;
	
	@Autowired
	private LessonService lessonService;
	
	@Autowired
	private MajorRepository majorRepository;
	
	@Autowired
	private StudentCourseRepository studentCourseRepository;
	
	@Autowired
	private StudentCourseService studentCourseService;
	
	@Autowired
	private ChapterRepository chapterRepository;
	
	public CourseResponse createCourse(CourseRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Teacher teacher = teacherRepository.findByAccountAndDeletedDateIsNull(account);
		
		Major major = majorRepository.findById(request.getMajorId()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		
		String status = switch (request.getStatus()) {
        case "PRIVATE" -> StatusCourse.PRIVATE.name();
        case "PUBLIC" -> StatusCourse.PUBLIC.name();
        case "REQUEST" -> StatusCourse.REQUEST.name();
        default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
		};
		
		String learningDurationType = switch (request.getLearningDurationType()) {
		case "LIMITED" -> CourseLearningDurationType.LIMITED.name();
		case "UNLIMITED" -> CourseLearningDurationType.UNLIMITED.name();
		default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
		};
		
		String feeType = switch (request.getFeeType()) {
		case "CHARGEABLE" -> FeeStatus.CHARGEABLE.name();
		case "NON_CHARGEABLE" -> FeeStatus.NON_CHARGEABLE.name();
		default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
		};
		
		Course course = courseMapper.toCourse(request);
				course.setTeacher(teacher);
				course.setCreatedBy(name);
				course.setStatus(status);
				course.setStartDate(request.getStartDate());
				course.setLearningDurationType(learningDurationType);
				course.setFeeType(feeType);
				course.setMajor(major.getName());
				course.setCreatedDate(new Date());	
		if(learningDurationType.equals(CourseLearningDurationType.LIMITED.name())) {
			if(request.getEndDate() != null){
				course.setEndDate(request.getEndDate());
			}
			else {
				throw new AppException(ErrorCode.NOT_NULL);
			}
		}
		else {
			course.setEndDate(null);
		}
		if(status.equals(StatusCourse.REQUEST.name())) {
			if(feeType.equals(FeeStatus.CHARGEABLE.name())) {
				if(request.getPrice() != null) {
					course.setPrice(request.getPrice());				
				}
				else {
					throw new AppException(ErrorCode.NOT_NULL);
				}
			}
			else {
				course.setPrice(null);
			}
		}
		else {
			course.setPrice(null);
		}
		
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
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Teacher teacher = teacherRepository.findByAccountAndDeletedDateIsNull(account);
	
		
		Major major = majorRepository.findById(request.getMajorId()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		
		String status = switch (request.getStatus()) {
        case "PRIVATE" -> StatusCourse.PRIVATE.name();
        case "PUBLIC" -> StatusCourse.PUBLIC.name();
        case "REQUEST" -> StatusCourse.REQUEST.name();
        default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
		};
		
		String learningDurationType = switch (request.getLearningDurationType()) {
		case "LIMITED" -> CourseLearningDurationType.LIMITED.name();
		case "UNLIMITED" -> CourseLearningDurationType.UNLIMITED.name();
		default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
		};
		
		String feeType = switch (request.getFeeType()) {
		case "CHARGEABLE" -> FeeStatus.CHARGEABLE.name();
		case "NON_CHARGEABLE" -> FeeStatus.NON_CHARGEABLE.name();
		default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
		};
		
		Course course = courseRepository.findByIdAndTeacherAndDeletedDateIsNull(request.getIdCourse(), teacher);
		if(course == null) {
			throw new AppException(ErrorCode.COURSE_NOT_FOUND);
		}
		course.setName(request.getName());
		course.setDescription(request.getDescription());
		course.setMajor(major.getName());
		course.setStatus(status);
		course.setStartDate(request.getStartDate());
		course.setLearningDurationType(learningDurationType);
		course.setFeeType(feeType);
		course.setLastModifiedBy(name);
		course.setLastModifiedDate(new Date());
		if(learningDurationType.equals(CourseLearningDurationType.LIMITED.name())) {
			if(request.getEndDate() != null){
				course.setEndDate(request.getEndDate());
			}
			else {
				throw new AppException(ErrorCode.NOT_NULL);
			}
		}
		else {
			course.setEndDate(null);
		}
		if(status.equals(StatusCourse.REQUEST.name())) {
			if(feeType.equals(FeeStatus.CHARGEABLE.name())) {
				if(request.getPrice() != null) {
					course.setPrice(request.getPrice());				
				}
				else {
					throw new AppException(ErrorCode.NOT_NULL);
				}
			}
			else {
				course.setPrice(null);
			}
		}
		else {
			course.setPrice(null);
		}
		course = courseRepository.save(course);
		return courseMapper.toCourseResponse(course);
	}
	
	public boolean deleteCourse(String id) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Course course = courseRepository.findByIdAndDeletedDateIsNull(id);
		List<StudentCourse> listStudentCourse = studentCourseRepository.findByCourseAndDeletedDateIsNull(course);
		if(listStudentCourse != null && !listStudentCourse.isEmpty()) {			
			studentCourseService.deleteListStudentBeforeDeleteCourse(listStudentCourse);
		}
		if(course != null) {
			lessonService.deleteLessonByCourse(course);
			course.setDeletedBy(name);
			course.setDeletedDate(new Date());
			courseRepository.save(course);
			return true;
		}
		return false;
	}
	
	public Page<CourseViewResponse> getAllPublicCourse(int pageNumber , int pageSize){
	    Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Course> courses = courseRepository.findByStatusInAndDeletedDateIsNull(Arrays.asList(StatusCourse.PUBLIC.name(), StatusCourse.REQUEST.name()), pageable);

		Page<CourseViewResponse> courseResponsePage = courses.map(course -> {
	        CourseViewResponse cr = courseMapper.toCourseViewResponse(course);
	        cr.setStudentCount(studentCourseRepository.countStudentsByCourse(course));
	        cr.setChapterCount(chapterRepository.countChaptersByCourse(course));
	        return cr;
	    });
		
		return courseResponsePage;
	}
	
	public Page<CourseViewResponse> getAllPublicCourseSortByMajor(int page , int size){
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
	
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Course> courses = courseRepository.findAllOrderByMatchingMajorFirst(student.getMajor().getName(), pageable);
		
		Page<CourseViewResponse> courseResponsePage = courses.map(course -> {
			CourseViewResponse cr = courseMapper.toCourseViewResponse(course);
			cr.setStudentCount(studentCourseRepository.countStudentsByCourse(course));
			cr.setChapterCount(chapterRepository.countChaptersByCourse(course));
			return cr;
		});
		
		return courseResponsePage;
	}
	
	public Page<CourseViewResponse> searchCourses(String courseName, String teacherName, int pageNumber , int pageSize) {
	   
	    Pageable pageable = PageRequest.of(pageNumber, pageSize);
	    
	    Page<Course> cPage = courseRepository.searchByCourseNameAndTeacherName(courseName, teacherName,pageable);
	    
	    return cPage.map(courseMapper::toCourseViewResponse);
	}

	
	public Page<CourseViewResponse> getCourseOfTeacher(int page , int size){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Teacher teacher = teacherRepository.findByAccountAndDeletedDateIsNull(account);
	    
	    Pageable pageable = PageRequest.of(page, size);
		
		
		Page<Course> courses = courseRepository.findByTeacherAndDeletedDateIsNull(teacher, pageable);
		Page<CourseViewResponse> courseResponsePage = courses.map(course -> {
	        CourseViewResponse cr = courseMapper.toCourseViewResponse(course);
	        cr.setStudentCount(studentCourseRepository.countStudentsByCourse(course));
	        cr.setChapterCount(chapterRepository.countChaptersByCourse(course));
	        return cr;
	    });
		
		return courseResponsePage;
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
