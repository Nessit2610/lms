package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentViewResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.JoinClassRequest;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentCourse;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.FeeStatus;
import com.husc.lms.enums.JoinClassStatus;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.enums.StatusCourse;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.JoinClassRequestRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class JoinClassRequestService {

	@Autowired
	private JoinClassRequestRepository joinClassRequestRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentCourseRepository studentCourseRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private StudentCourseService studentCourseService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Transactional
	public boolean pendingRequest(String courseId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));;
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if (course.getEndDate() != null) {
		    Date now = new Date();
		    if (course.getEndDate().before(now)) {
		        throw new AppException(ErrorCode.COURSE_ENDED);
		    }
		}
		if (course.getStartDate() != null) {
		    Date now = new Date();
		    if (course.getStartDate().after(now)) {
		        throw new AppException(ErrorCode.COURSE_NOT_STARTED);
		    }
		}
		
		if(course.getStatus().contains(StatusCourse.PRIVATE.name())) {
			throw new AppException(ErrorCode.CAN_NOT_REQUEST);
		}
		
		if(joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.PENDING.name())
		|| joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.APPROVED.name())) {
			throw new AppException(ErrorCode.REQUEST_EXIST);
		}
	
		if(joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.REJECTED.name())) {
			joinClassRequestRepository.deleteByStudentAndCourse(student, course);
		}
		if(student != null && course != null) {
			if(course.getStatus().equals(StatusCourse.PUBLIC.name())) {
				JoinClassRequest joinClassRequest = JoinClassRequest.builder()
						.student(student)
						.course(course)
						.status(JoinClassStatus.APPROVED.name())
						.createdAt(new Date())
						.build();
				joinClassRequestRepository.save(joinClassRequest);
				studentCourseService.addStudentToCourse(student, course);
				
				Notification notificationForTeacher = Notification.builder()
															.account(course.getTeacher().getAccount())
															.description("Bạn có 1 sinh viên tên " + student.getFullName() + " vừa đăng ký vào khoá học " + course.getName())
															.joinClassRequest(joinClassRequest)
															.type(NotificationType.JOIN_CLASS_PENDING.name())
															.createdAt(OffsetDateTime.now())
															.build();
				notificationRepository.save(notificationForTeacher);
				
				// Gửi thông báo riêng cho teacher qua WebSocket
                Map<String, Object> teacherPayload = new HashMap<>();
                teacherPayload.put("receivedAccount", course.getTeacher().getAccount().getUsername());
                teacherPayload.put("message", "Bạn có 1 sinh viên tên " + student.getFullName() + " vừa đăng ký vào khoá học " + course.getName());
                teacherPayload.put("type", NotificationType.JOIN_CLASS_PENDING.name());
                teacherPayload.put("joinClassRequestId", joinClassRequest.getId());
                teacherPayload.put("createdAt", OffsetDateTime.now());

                notificationService.sendCustomWebSocketNotificationToUser(course.getTeacher().getAccount().getUsername(),teacherPayload);
				return true;
			}
			else {
				if(course.getFeeType().equals(FeeStatus.CHARGEABLE.name())) {
					throw new AppException(ErrorCode.CHARGEABLE_COURSE);
				}
				else {
					JoinClassRequest joinClassRequest = JoinClassRequest.builder()
							.student(student)
							.course(course)
							.status(JoinClassStatus.PENDING.name())
							.createdAt(new Date())
							.build();
					joinClassRequestRepository.save(joinClassRequest);
					
					Notification notificationForTeacher = Notification.builder()
							.account(course.getTeacher().getAccount())
							.description("Bạn có 1 sinh viên tên " + student.getFullName() + " vừa đăng ký vào khoá học " + course.getName())
							.joinClassRequest(joinClassRequest)
							.type(NotificationType.JOIN_CLASS_PENDING.name())
							.createdAt(OffsetDateTime.now())
							.build();
					notificationRepository.save(notificationForTeacher);
					
					// Gửi thông báo riêng cho teacher qua WebSocket
					Map<String, Object> teacherPayload = new HashMap<>();
					teacherPayload.put("receivedAccount", course.getTeacher().getAccount().getUsername());
					teacherPayload.put("message", "Bạn có 1 sinh viên tên " + student.getFullName() + " đang chờ duyệt vào khoáss " + course.getName());
					teacherPayload.put("type", NotificationType.JOIN_CLASS_PENDING.name());
					teacherPayload.put("joinClassRequestId", joinClassRequest.getId());
					teacherPayload.put("createdAt", OffsetDateTime.now());
					
					notificationService.sendCustomWebSocketNotificationToUser(course.getTeacher().getAccount().getUsername(),teacherPayload);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean rejectedRequest(String courseId, String studentId) {
		
		Student student = studentRepository.findById(studentId).get();
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if(student != null && course != null) {
			JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
			joinClassRequest.setStatus(JoinClassStatus.REJECTED.name());
			joinClassRequestRepository.save(joinClassRequest);
			
			Notification notificationForStudent = Notification.builder()
					.account(student.getAccount())
					.description("Bạn đã bị giảng viên " + course.getTeacher().getFullName() + " từ chối gia nhập khoá học " + course.getName())
					.joinClassRequest(joinClassRequest)
					.type(NotificationType.JOIN_CLASS_REJECTED.name())
					.createdAt(OffsetDateTime.now())
					.build();
			notificationRepository.save(notificationForStudent);
			
			// Gửi thông báo riêng cho teacher qua WebSocket
			Map<String, Object> teacherPayload = new HashMap<>();
			teacherPayload.put("receivedAccount", joinClassRequest.getStudent().getAccount().getUsername());
			teacherPayload.put("message", "Bạn đã bị giảng viên " + course.getTeacher().getFullName() + " từ chối gia nhập khoá học " + course.getName());
			teacherPayload.put("type", NotificationType.JOIN_CLASS_REJECTED.name());
			teacherPayload.put("joinClassRequestId", joinClassRequest.getId());
			teacherPayload.put("createdAt", OffsetDateTime.now());
			
			notificationService.sendCustomWebSocketNotificationToUser(joinClassRequest.getStudent().getAccount().getUsername(),teacherPayload);
			return true;
		}
		return false;
	}
	
	public boolean approvedRequest(String courseId,String studentId) {
		Student student = studentRepository.findById(studentId).get();
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if(student != null && course != null) {
			JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
			joinClassRequest.setStatus(JoinClassStatus.APPROVED.name());
			joinClassRequestRepository.save(joinClassRequest);
			studentCourseService.addStudentToCourse(student, course);
			
			Notification notificationForStudent = Notification.builder()
					.account(student.getAccount())
					.description("Bạn đã được giảng viên " + course.getTeacher().getFullName() + " duyệt đơn xin gia nhập khoá học " + course.getName())
					.joinClassRequest(joinClassRequest)
					.type(NotificationType.JOIN_CLASS_APPROVED.name())
					.createdAt(OffsetDateTime.now())
					.build();
			notificationRepository.save(notificationForStudent);
			
			// Gửi thông báo riêng cho teacher qua WebSocket
			Map<String, Object> teacherPayload = new HashMap<>();
			teacherPayload.put("receivedAccount", joinClassRequest.getStudent().getAccount().getUsername());
			teacherPayload.put("message", "Bạn đã được giảng viên " + course.getTeacher().getFullName() + " duyệt đơn xin gia nhập khoá học " + course.getName());
			teacherPayload.put("type", NotificationType.JOIN_CLASS_APPROVED.name());
			teacherPayload.put("joinClassRequestId", joinClassRequest.getId());
			teacherPayload.put("createdAt", OffsetDateTime.now());
			
			notificationService.sendCustomWebSocketNotificationToUser(joinClassRequest.getStudent().getAccount().getUsername(),teacherPayload);
			return true;
		}
		return false;
	}
	
	public Page<StudentViewResponse> getAllStudentRequestOfCourse(String courseId, int page, int size){
	    
	    Pageable pageable = PageRequest.of(page, size);
	    
		Page<Student> students = joinClassRequestRepository.findAllStudentsByCourseIdAndStatus(courseId,JoinClassStatus.PENDING.name(),pageable);
		return students.map(studentMapper::toStudentViewResponse);
	}
	
	public Page<CourseViewResponse> getAllCourseRequestOfStudent(String studentId,int page, int size){
	    
	    Pageable pageable = PageRequest.of(page, size);
		
		Page<Course> courses = joinClassRequestRepository.findAllCoursesByStudentIdAndStatus(studentId, JoinClassStatus.PENDING.name(),pageable);
		Page<CourseViewResponse> courseResponses = courses.map(course -> {
	        CourseViewResponse cr = courseMapper.toCourseViewResponse(course);
	        cr.setStudentCount(studentCourseRepository.countStudentsByCourse(course));
	        cr.setChapterCount(lessonRepository.countLessonsByCourse(course));
	        return cr;
	    });
		return courseResponses;	
	}
	
	public String getStatusJoinClass(String courseId) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));;
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if(student != null && course != null) {
			StudentCourse studentCourse = studentCourseRepository.findByCourseAndStudentAndDeletedDateIsNull(course, student);
			JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
			if(studentCourse != null) {
				return JoinClassStatus.APPROVED.name();
			}
			else {
				if(joinClassRequest != null) {
					return joinClassRequest.getStatus();
				}
				else {
					return JoinClassStatus.NOT_JOINED.name();	
				}
			}
		}
		return JoinClassStatus.NOT_JOINED.name();
	}
	
}
