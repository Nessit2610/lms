package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.DocumentResponse;
import com.husc.lms.entity.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
	DocumentResponse toDocumentResponse(Document document);
}
