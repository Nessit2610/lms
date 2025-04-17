package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
	
	public MajorResponse createMajor(String majorName) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Major major = Major.builder()
				.name(majorName)
				.createdBy(name)
				.createdDate(new Date())
				.build();
		majorRepository.save(major);
		return majorMapper.toMajorResponse(major);
	}
	
	public List<MajorResponse> getAllMajor(){
		List<Major> majors = majorRepository.findAllActiveMajors();
		return majors.stream().map(majorMapper::toMajorResponse).toList();
	}
	
	public boolean deleteMajor(String id) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Major major = majorRepository.findById(id).get();
		if(major != null) {
			major.setDeletedBy(name);
			major.setDeletedDate(new Date());
			major = majorRepository.save(major);
			return true;
		}
		return false;
	}
}
