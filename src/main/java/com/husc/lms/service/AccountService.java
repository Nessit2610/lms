package com.husc.lms.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.husc.lms.constant.PredefinedRole;
import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.entity.Role;
import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.AccountMapper;
import com.husc.lms.repository.RoleRepository;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
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
	

	public AccountResponse createAccountStudent(AccountRequest request) {
		
		if(accountRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = new Account();
		account.setUsername(request.getUsername());
		account.setEmail(request.getEmail());
		account.setActive(true);
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		account.setCreatedDate(new Date());
		account.setCreatedBy(name);
		
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.STUDENT_ROLE).ifPresent(roles::add);
        account.setRoles(roles);

        try {
        	account = accountRepository.save(account);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }

        return accountMapper.toAccountResponse(account);
		 
	}
	
	public AccountResponse createAccountTeacher(AccountRequest request) {
		
		if(accountRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = new Account();
		account.setUsername(request.getUsername());
		account.setEmail(request.getEmail());
		account.setActive(true);
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		account.setCreatedDate(new Date());
		account.setCreatedBy(name);
		
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.TEACHER_ROLE).ifPresent(roles::add);
        account.setRoles(roles);

        try {
        	account = accountRepository.save(account);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }

        return accountMapper.toAccountResponse(account);
		
	}
	
		
	@PreAuthorize("hasRole('ADMIN')")
	public List<AccountResponse> GetAllUser() {
		return accountRepository.findAll().stream().map(accountMapper :: toAccountResponse).toList();	
	}

	@PostAuthorize("returnObject.username == authentication.name")
	public AccountResponse GetUserById(String id) {
		return accountMapper.toAccountResponse(accountRepository.findById(id).orElseThrow(() -> new RuntimeException()));
	}
	
	
	public AccountResponse changePassword(PasswordRequest request) {
	    var context = SecurityContextHolder.getContext();
	    String username = context.getAuthentication().getName();

	    Account user = accountRepository.findByUsername(username).orElseThrow( () -> new AppException(ErrorCode.USER_NOTFOUND));

	    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
	        throw new AppException(ErrorCode.OLD_PASSWORD_NOT);
	    }

	    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
	    accountRepository.save(user);

	    return accountMapper.toAccountResponse(user);
	}

	
	public void DeleteUser(String id) {
		accountRepository.deleteById(id);
	}

}
