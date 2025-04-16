package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.ChapterRequest;
import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.entity.Chapter;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
  
	public Chapter toChapter(ChapterRequest request);
	
	public ChapterResponse toChapterResponse(Chapter chapter);
}
