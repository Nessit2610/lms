package com.husc.lms.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.response.LearningPlanOfStudent;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Class;
import com.husc.lms.entity.Major;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.User;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.MajorMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.ClassRepository;
import com.husc.lms.repository.MajorRepository;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.UserRepository;


@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MajorRepository majorRepository;
	
	@Autowired
	private ClassRepository classRepository;
	
	@Autowired
	private UserService userService;
	
	public StudentResponse createStudent(StudentRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Major major = majorRepository.findById(request.getIdmajor()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		UserCreationRequest uRequest = UserCreationRequest.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		UserResponse userResponse = userService.createUserStudent(uRequest);
		User user = userRepository.findById(userResponse.getId()).get();
		Class clazz = classRepository.findById(request.getIdclass()).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		Student student = studentMapper.toStudent(request);
		student.setUser(user);
		student.setCode("TESTCODE");
		student.setMajor(major);
		student.setClazz(clazz);
		student.setCreatedBy(name);
		student.setCreatedDate(new Date());
		student.setLastModifiedBy(name);
		student.setLastModifiedDate(new Date());
		student = studentRepository.save(student);
		
		return studentMapper.toStudentResponse(student);
	}
	
	public List<StudentResponse> getAllStudent(){
		return studentRepository.findAll().stream().map(studentMapper::toStudentResponse).toList();
	}
	
	public StudentResponse getStudentInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).get();
		Student student = studentRepository.findByUser(user);
		return studentMapper.toStudentResponse(student);
		
	}
	
	public LearningPlanOfStudent getLearningPlanOfStudent() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).get();
		Student student = studentRepository.findByUser(user);
		return studentMapper.toLearningPlanOfStudent(student);
	}
	
	public String uploadPhoto(String id, MultipartFile file) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		String photoUrl = photoFunction.apply(id, file);
		student.setAvatar(photoUrl);
		studentRepository.save(student);
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
			return ServletUriComponentsBuilder.fromCurrentContextPath().path("/student/image/" + filename).toUriString();
		} catch (Exception e) {
			throw new RuntimeException("Unable to save image");
		}
	};


}
