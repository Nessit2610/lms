package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Major;
import com.husc.lms.repository.MajorRepository;

@Service
public class MajorService {

	@Autowired
	private MajorRepository majorRepository;
	
	public void createMajor(String majorName) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Major major = Major.builder()
				.name(majorName)
				.createdBy(name)
				.createdDate(new Date())
				.build();
		majorRepository.save(major);
	}
}
