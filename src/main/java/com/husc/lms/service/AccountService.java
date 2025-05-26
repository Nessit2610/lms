package com.husc.lms.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.husc.lms.constant.PredefinedRole;
import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.entity.Role;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Teacher;
import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.Roles;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.AccountMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.mapper.TeacherMapper;
import com.husc.lms.repository.RoleRepository;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.TeacherRepository;

import jakarta.transaction.Transactional;

import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	@Autowired
	private final AccountRepository accountRepository;
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private TeacherMapper teacherMapper;
	
	//PUBLIC
	
	public AccountResponse createAccountStudent(AccountRequest request) {
		
		if(accountRepository.existsByUsername(request.getEmail())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}    
		Account account = new Account();
		account.setUsername(request.getEmail());
		account.setEmail(request.getEmail());
		account.setActive(true);
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		account.setCreatedDate(new Date());
		account.setCreatedBy(request.getEmail());
		
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.STUDENT_ROLE).ifPresent(roles::add);
        account.setRoles(roles);

        account = accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
		 
	}
	
	public AccountResponse createAccountTeacher(AccountRequest request) {
		
		if(accountRepository.existsByUsername(request.getEmail())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		
		Account account = new Account();
		account.setUsername(request.getEmail());
		account.setEmail(request.getEmail());
		account.setActive(true);
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		account.setCreatedDate(new Date());
		account.setCreatedBy(request.getEmail());
		
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.TEACHER_ROLE).ifPresent(roles::add);
        account.setRoles(roles);
        
    	account = accountRepository.save(account);
        return accountMapper.toAccountResponse(account);
		
	}
	public void changePassword(PasswordRequest request) {
	    var context = SecurityContextHolder.getContext();
	    String username = context.getAuthentication().getName();

	    Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username).orElseThrow( () -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

	    if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
	        throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
	    }
	    account.setPassword(passwordEncoder.encode(request.getNewPassword()));
	    accountRepository.save(account);
	}

	//ROLE ADMIN
	
	public Page<AccountResponse> getAllAccount(int page , int size){
		Pageable pageable = PageRequest.of(page, size);
		return accountRepository.findAll(pageable).map(accountMapper::toAccountResponse);
	}
	
	
	public Object getAccountDetails(String accountId){
		List<String> roles = accountRepository.findRoleNamesByAccountId(accountId);
		Account account = accountRepository.findById(accountId).get();
		if(roles.contains(Roles.STUDENT.name())) {
			Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
			return studentMapper.toStudentResponse(student);
		}
		else if(roles.contains(Roles.TEACHER.name())) {
			Teacher teacher = teacherRepository.findByAccountAndDeletedDateIsNull(account);
			return teacherMapper.toTeacherResponse(teacher);
		}
		return null;
	}
	
	
	public AccountResponse getAccountById(String id) {
		Account account = accountRepository.findById(id).get();
		return accountMapper.toAccountResponse(account);
	}
	
	public Boolean setActiveAccount(String accountId, boolean active) {
		Account account = accountRepository.findById(accountId).get();
		account.setActive(active);
		accountRepository.save(account);
		return true;
	}
	
	@Transactional
    public Account updateAccountRoles(String accountId, Set<String> roleIds) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + id)))
                .collect(Collectors.toSet());

        account.setRoles(roles);

        return accountRepository.save(account);
    }
	
}
