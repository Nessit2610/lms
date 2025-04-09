package com.husc.lms.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.dto.response.TeacherInfoResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Teacher;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.TeacherMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TeacherMapper teacherMapper;
	
	@Autowired
	private AccountService accountService;
	
	public TeacherResponse createTeacher(TeacherRequest request) {
		
		AccountRequest uRequest = AccountRequest.builder()
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		AccountResponse accountResponse = accountService.createAccountTeacher(uRequest);
		Account account = accountRepository.findById(accountResponse.getId()).get();
		
		Teacher teacher = teacherMapper.toTeacher(request);
				teacher.setAccount(account);
		teacher = teacherRepository.save(teacher);
		return teacherMapper.toTeacherResponse(teacher);
	}
	
	public TeacherInfoResponse getTeacherInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		return teacherMapper.toTeacherInfoResponse(teacher);
	}
	
	public String uploadPhoto(String id, MultipartFile file) {
		Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		String photoUrl = photoFunction.apply(id, file);
		teacher.setAvatar(photoUrl);
		teacherRepository.save(teacher);
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
			return ServletUriComponentsBuilder.fromCurrentContextPath().path("/teacher/image/" + filename).toUriString();
		} catch (Exception e) {
			throw new RuntimeException("Unable to save image");
		}
	};
	
	public List<TeacherResponse> getAllTeacher(){
		return teacherRepository.findAll().stream().map(teacherMapper::toTeacherResponse).toList();
	}
}
