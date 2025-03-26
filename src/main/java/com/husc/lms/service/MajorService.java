package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.MajorRequest;
import com.husc.lms.dto.response.MajorResponse;
import com.husc.lms.entity.Major;
import com.husc.lms.mapper.MajorMapper;
import com.husc.lms.repository.MajorRepository;

@Service
public class MajorService {

	@Autowired
	private MajorRepository majorRepository;
	
	@Autowired
	private MajorMapper majorMapper;
	
	
	
	public MajorResponse createMajor(MajorRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Major major = majorMapper.toMajor(request);
		major.setCreatedBy(name);
		major.setCreatedDate(new Date());
		major.setLastModifiedBy(name);
		major.setLastModifiedDate(new Date());
		major = majorRepository.save(major);
		return majorMapper.toMajorResponse(major);
		
	}
	
	
	public List<MajorResponse> getAllMajor(){
		return majorRepository.findAll().stream().map(majorMapper::toMajorResponse).toList();
	}
}
