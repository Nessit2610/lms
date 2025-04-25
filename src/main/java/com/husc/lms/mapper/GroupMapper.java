package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.GroupRequest;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.entity.Group;

@Mapper(componentModel = "spring")
public interface GroupMapper {

	GroupViewResponse toGroupViewResponse(Group group);
	
	Group toGroup(GroupRequest request);
	
}
