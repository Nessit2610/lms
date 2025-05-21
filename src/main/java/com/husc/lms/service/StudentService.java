package com.husc.lms.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.dto.response.StudentViewResponse;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Major;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ConfirmationCodeRepository;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.MajorRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;


@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ConfirmationCodeRepository codeRepository;
	
	@Autowired
	private StudentLessonChapterProgressRepository studentLessonChapterProgressRepository;
	
	@Autowired
	private StudentLessonProgressRepository studentLessonProgressRepository;

	@Autowired
	private MajorRepository majorRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	
	@Autowired
	private AccountService accountService;
	
	public StudentResponse createStudent(StudentRequest request) {
		
		if(accountRepository.existsByUsername(request.getEmail())) {
			throw new AppException(ErrorCode.EMAIL_ALREADY_USED);
		} 
		
		if(codeRepository.findByEmail(request.getEmail()) == null || codeRepository.findByEmail(request.getEmail()).isVerify() == false) {
			throw new AppException(ErrorCode.EMAIL_NOTCONFIRM);
		}
		
		Major major = majorRepository.findById(request.getMajorId()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		
		AccountRequest uRequest = AccountRequest.builder()
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		AccountResponse accountResponse = accountService.createAccountStudent(uRequest);
		Account account = accountRepository.findById(accountResponse.getId()).get();
		
		Student student = studentMapper.toStudent(request);
				student.setAccount(account);
				student.setMajor(major);
		student = studentRepository.save(student);
		
		return studentMapper.toStudentResponse(student);
	}
	
	public Page<StudentViewResponse> searchStudents(String fullName, String email, String majorName, int page, int size) {
	    if (fullName != null && fullName.trim().isEmpty()) fullName = null;
	    if (email != null && email.trim().isEmpty()) email = null;
	    if (majorName != null && majorName.trim().isEmpty()) majorName = null;
	    
	    Pageable pageable = PageRequest.of(page, size);
	    
	    return studentRepository.searchStudent(fullName, email, majorName, pageable).map(studentMapper::toStudentViewResponse);
	}

	public Page<StudentViewResponse> searchStudentsNotInGroup(String groupId, String keyword, int page, int size) {
		Group group = groupRepository.findById(groupId).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Student> student = studentRepository.findStudentsNotInGroup(group, keyword, pageable);

	    return student.map(studentMapper::toStudentViewResponse);
	}
	
	public StudentResponse getStudentInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		return studentMapper.toStudentResponse(student);
		
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
			return "/lms/student/image/" + filename;
		} catch (Exception e) {
			throw new RuntimeException("Unable to save image"); 
		}
	};

	public List<Student> findEligibleStudents(String lessonId, String chapterId) {
        List<String> studentIdsByLesson = studentLessonProgressRepository.findStudentIdsByLessonCompleted(lessonId);
        List<String> studentIdsByChapter = studentLessonChapterProgressRepository.findStudentIdsByChapterCompleted(chapterId);

        Set<String> uniqueStudentIds = new HashSet<>();
        uniqueStudentIds.addAll(studentIdsByLesson);
        uniqueStudentIds.addAll(studentIdsByChapter);

        return studentRepository.findAllById(uniqueStudentIds);
    }
}
