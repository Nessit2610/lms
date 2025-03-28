package com.husc.lms.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Major;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.User;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.MajorMapper;
import com.husc.lms.mapper.StudentMapper;
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
	private UserService userService;
	
	public StudentResponse createStudent(StudentRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Major major = majorRepository.findById(request.getIdmajor()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		System.out.println(major.getCode());
		UserCreationRequest uRequest = UserCreationRequest.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		UserResponse userResponse = userService.createUserStudent(uRequest);
		Student student = studentMapper.toStudent(request);
		student.setUserId(userResponse.getId());
		student.setCode("TESTCODE");
		student.setCreatedBy(name);
		student.setMajorId(major);
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
		Student student = studentRepository.findByUserId(user.getId());
		return studentMapper.toStudentResponse(student);
		
	}
	
	public String uploadPhoto(String id, MultipartFile file) {
	    Student student = studentRepository.findById(id)
	            .orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

	    try {
	        // Đọc ảnh từ file
	        BufferedImage originalImage = ImageIO.read(file.getInputStream());

	        // Resize ảnh (giảm kích thước để tối ưu)
	        int targetWidth = 100;  // Điều chỉnh kích thước theo nhu cầu
	        int targetHeight = 100;
	        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = resizedImage.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
	        g.dispose();

	        // Chuyển ảnh thành byte[]
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(resizedImage, "jpg", baos);  // Chuyển sang định dạng JPEG
	        byte[] photoBytes = baos.toByteArray();

	        // Lưu vào database
	        student.setAvatar(photoBytes);
	        studentRepository.save(student);

	        return "Photo uploaded successfully";
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to upload photo: " + e.getMessage());
	    }
	}


}
