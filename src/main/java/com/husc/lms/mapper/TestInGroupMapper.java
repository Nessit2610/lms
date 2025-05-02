package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.TestInGroupResponse;
import com.husc.lms.entity.TestInGroup;

@Mapper(componentModel = "spring")
public interface TestInGroupMapper {

	TestInGroupResponse toTestInGroupResponse(TestInGroup testInGroup);
}
