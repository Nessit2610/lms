package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.MajorResponse;
import com.husc.lms.entity.Major;

@Mapper(componentModel = "spring")
public interface MajorMapper {

	MajorResponse toMajorResponse(Major major);
}
