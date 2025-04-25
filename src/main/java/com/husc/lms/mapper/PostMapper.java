package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.PostResponse;
import com.husc.lms.entity.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

	PostResponse toPostResponse(Post post);
}
