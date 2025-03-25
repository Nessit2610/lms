package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.entity.Course;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.repository.CourseRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseMapper courseMapper;

	public CourseResponse createCourse(CourseRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Course course = Course.builder()
				.name(request.getName())
				.startDate(request.getStartDate())
				.endDate(request.getEndDate())
				.majorId(request.getMajorId())
				.createdBy(name)
				.createdDate(new Date())
				.lastModifiedBy(name)
				.lastModifiedDate(new Date())
				.build();
		course = courseRepository.save(course);
		CourseResponse courseResponse = CourseResponse.builder()
				.name(course.getName())
				.startDate(course.getStartDate())
				.endDate(course.getEndDate())
				.majorId(course.getMajorId())
				.build();
		return courseResponse;
	}
	
	public List<CourseResponse> getAllCourse(){
		return courseRepository.findAll().stream().map(courseMapper::toCourseResponse).toList();
	}
}
