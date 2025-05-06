package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.PostFileResponse;
import com.husc.lms.entity.PostFile;

@Mapper(componentModel = "spring")
public interface PostFileMapper {
	PostFileResponse toPostFileResponse(PostFile postFile);
}
