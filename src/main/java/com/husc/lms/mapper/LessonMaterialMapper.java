package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.LessonMaterialResponse;
import com.husc.lms.entity.LessonMaterial;

@Mapper(componentModel = "spring")
public interface LessonMaterialMapper {

	public LessonMaterialResponse toLessonMaterialResponse(LessonMaterial lessonMaterial);
}
