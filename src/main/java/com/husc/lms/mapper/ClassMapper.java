package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.ClassResponse;
import com.husc.lms.entity.Class;

@Mapper(componentModel = "spring")
public interface ClassMapper {

	public ClassResponse toClassResponse(Class c);
	
}
