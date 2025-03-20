package com.husc.lms.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.husc.lms.constant.PredefinedRole;
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.request.UserUpdateRequest;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Role;
import com.husc.lms.entity.User;
import com.husc.lms.entity.UserMongo;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.UserMapper;
import com.husc.lms.repository.RoleRepository;
import com.husc.lms.repository.UserMongoRepository;
import com.husc.lms.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class UserService {
	
	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private final UserMongoRepository userMongoRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	public UserService(UserRepository userRepository, UserMongoRepository userMongoRepository, UserMapper userMapper) {
		super();
		this.userRepository = userRepository;
		this.userMongoRepository = userMongoRepository;
		this.userMapper = userMapper;
	}

	public UserResponse createUser(UserCreationRequest request) {
		
		if(userRepository.existsByUsername(request.getUSERNAME())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		
		User user = new User();
		user.setEmail(request.getEMAIL());
		user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.getPASSWORD()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);


        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
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
	
	
	public UserResponse UpdateUser(String userId, UserUpdateRequest updateRequest) {
		User user = userRepository.findById(userId).orElseThrow( () -> new AppException(ErrorCode.USER_NOTFOUND));
		
	
		user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
		
		var roles = roleRepository.findAllById(updateRequest.getRoles());
		
		
		return userMapper.toUserResponse(userRepository.save(user));
	}
	
	public void DeleteUser(String id) {
		userRepository.deleteById(id);
	}
	// Lấy tất cả users từ MongoDB
    public List<UserMongo> getAllUsersMongo() {
        return userMongoRepository.findAll();
    }
    // Lưu user vào MongoDB
    public UserMongo saveUserToMongo(UserMongo user) {
        return userMongoRepository.save(user);
    }
}
