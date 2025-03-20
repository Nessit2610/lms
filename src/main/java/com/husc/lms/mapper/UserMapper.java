package com.husc.lms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.request.UserUpdateRequest;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.User;


@Mapper(componentModel = "spring")
public interface UserMapper{
	public User toUser(UserCreationRequest request);

	
	public UserResponse toUserResponse(User user);
}
