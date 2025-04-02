package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.SubjectMaterialResponse;

import com.husc.lms.entity.SubjectMaterial;

@Mapper(componentModel = "spring")
public interface SubjectMaterialMapper {

	public SubjectMaterialResponse toSubjectMaterialResponse(SubjectMaterial subject);
}
