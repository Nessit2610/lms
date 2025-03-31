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
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.request.PasswordRequest;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Role;
import com.husc.lms.entity.User;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.UserMapper;
import com.husc.lms.repository.RoleRepository;
import com.husc.lms.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
	
	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	

	public UserResponse createUserStudent(UserCreationRequest request) {
		
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedDate(new Date());
        user.setCreatedBy(name);
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.STUDENT_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        user.setVersion(1);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }

        return userMapper.toUserResponse(user);
		 
	}
	
	public UserResponse createUserTeacher(UserCreationRequest request) {
		
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setActive(true);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedDate(new Date());
		user.setCreatedBy(name);
		HashSet<Role> roles = new HashSet<>();
		roleRepository.findById(PredefinedRole.TEACHER_ROLE).ifPresent(roles::add);
		user.setRoles(roles);
		user.setVersion(1);
		
		try {
			user = userRepository.save(user);
		} catch (DataIntegrityViolationException exception) {
			throw new AppException(ErrorCode.USER_NOTFOUND);
		}
		
		return userMapper.toUserResponse(user);
		
	}
	
	public UserResponse getMyInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).orElseThrow( () -> new AppException(ErrorCode.USER_NOTFOUND));
		return userMapper.toUserResponse(user);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> GetAllUser() {
		return userRepository.findAll().stream().map(userMapper :: toUserResponse).toList();	
	}

	@PostAuthorize("returnObject.username == authentication.name")
	public UserResponse GetUserById(String id) {
		return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException()));
	}
	
	
	public UserResponse changePassword(PasswordRequest request) {
	    var context = SecurityContextHolder.getContext();
	    String username = context.getAuthentication().getName();

	    User user = userRepository.findByUsername(username).orElseThrow( () -> new AppException(ErrorCode.USER_NOTFOUND));

	    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
	        throw new AppException(ErrorCode.OLD_PASSWORD_NOT);
	    }

	    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
	    userRepository.save(user);

	    return userMapper.toUserResponse(user);
	}

	
	public void DeleteUser(String id) {
		userRepository.deleteById(id);
	}

}
