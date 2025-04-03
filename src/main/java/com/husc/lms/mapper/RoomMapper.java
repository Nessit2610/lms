package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.RoomResponse;
import com.husc.lms.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

	public RoomResponse toResponse(Room room);
	
}
