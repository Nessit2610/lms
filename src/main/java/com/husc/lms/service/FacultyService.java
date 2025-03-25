package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.FacultyRequest;
import com.husc.lms.dto.response.FacultyResponse;
import com.husc.lms.entity.Faculty;
import com.husc.lms.repository.FacultyMapper;
import com.husc.lms.repository.FacultyRepository;

@Service
public class FacultyService {

	@Autowired
	private FacultyRepository facultyRepository;
	
	@Autowired
	private FacultyMapper facultyMapper;
	
	public FacultyResponse createFaculty(FacultyRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Faculty faculty = facultyMapper.toFaculty(request);
		faculty.setCreatedBy(name);
		faculty.setCreatedDate(new Date());
		faculty.setLastModifiedBy(name);
		faculty.setLastModifiedDate(new Date());
		faculty = facultyRepository.save(faculty);
		return facultyMapper.toFacultyResponse(faculty);
		
	}
	
	public List<FacultyResponse> getAllFaculty(){
		return facultyRepository.findAll().stream().map(facultyMapper::toFacultyResponse).toList();
	}
}
