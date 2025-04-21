package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.StudentLessonChapterProgressResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentLessonChapterProgress;
import com.husc.lms.mapper.StudentLessonChapterProgressMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentRepository;

@Service
public class StudentLessonChapterProgressService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ChapterRepository chapterRepository;
	
	@Autowired
	private StudentLessonChapterProgressRepository slcpRepository;
	
	@Autowired
	private StudentLessonChapterProgressMapper slcpMapper;
	
	public StudentLessonChapterProgressResponse saveChapterProgress(String chapterId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		Student student = studentRepository.findByAccount(account);
		Chapter chapter = chapterRepository.findById(chapterId).get();
		StudentLessonChapterProgress slcp = StudentLessonChapterProgress.builder()
				.chapter(chapter)
				.student(student)
				.isCompleted(false)
				.createdBy(name)
				.createdDate(new Date())
				.build();
		slcp = slcpRepository.save(slcp);
		return slcpMapper.toResponse(slcp);
	}
	public StudentLessonChapterProgressResponse setCompleteChapter(String chapterId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		Student student = studentRepository.findByAccount(account);
		Chapter chapter = chapterRepository.findById(chapterId).get();
		StudentLessonChapterProgress slcp = slcpRepository.findByStudentAndChapter(student, chapter);
		slcp.setIsCompleted(true);
		slcp.setCompleteAt(new Date());
		slcp.setLastModifiedBy(name);
		slcp.setLastModifiedDate(new Date());
		slcp = slcpRepository.save(slcp);
		return slcpMapper.toResponse(slcp);
	}
	
	public StudentLessonChapterProgressResponse getChapterProgress(String chapterId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		Student student = studentRepository.findByAccount(account);
		Chapter chapter = chapterRepository.findById(chapterId).get();
		StudentLessonChapterProgress slcp = slcpRepository.findByStudentAndChapter(student, chapter);
		if (slcp == null) {
			return StudentLessonChapterProgressResponse.builder()
					.isCompleted(null)
					.build();
		}
		return slcpMapper.toResponse(slcp);
	}
	
	
}
