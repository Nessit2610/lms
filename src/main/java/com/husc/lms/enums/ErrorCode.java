package com.husc.lms.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {

	NOTFOUND_API(404, "API not exist", HttpStatus.BAD_REQUEST),
	USER_EXISTED(1001, "User Existed", HttpStatus.BAD_REQUEST),
	UNCATEGORIZED_EXCEPTION(1002, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
	PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
	INVALID_KEY(1005, "Invalid message key", HttpStatus.BAD_REQUEST),
	USER_UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
	ROLE_NOT_FOUND(1009, "Role not found", HttpStatus.BAD_REQUEST),
	OLD_PASSWORD_INCORRECT(1010, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),
	MAJOR_NOT_FOUND(1011, "Major not found", HttpStatus.BAD_REQUEST),
	CODE_ERROR(1012, "Error just error", HttpStatus.BAD_REQUEST),
	FILE_SIZE_EXCEEDED(1013, "File upload exceeds the maximum limit of 10MB!", HttpStatus.BAD_REQUEST),
	DOB_INVALID(1014, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
	EMAIL_NOTCONFIRM(1015, "Email not confirm code", HttpStatus.BAD_REQUEST),
	EMAIL_INVALID(1015, "Email invalid", HttpStatus.BAD_REQUEST),
	REQUEST_EXIST(1016, "Request existed", HttpStatus.BAD_REQUEST),
	INVALID_EMAIL_DOMAIN(1017, "Email phải sử dụng tên miền @husc.edu.vn", HttpStatus.BAD_REQUEST),
	TIME_LIMITED(1018, "Time limited", HttpStatus.BAD_REQUEST),
	STUDENT_NOT_FOUND(1019, "Không tìm thấy sinh viên", HttpStatus.NOT_FOUND),
	TEACHER_NOT_FOUND(1020, "Không tìm thấy giảng viên", HttpStatus.NOT_FOUND),
	LESSON_NOT_FOUND(1021, "Không tìm thấy bài học", HttpStatus.NOT_FOUND),
	CHAPTER_NOT_FOUND(1022, "Không tìm thấy chương", HttpStatus.NOT_FOUND),
	CHAPTER_ORDER_DUPLICATE(1023, "Thứ tự chương đã tồn tại trong bài học", HttpStatus.BAD_REQUEST),
	EMAIL_ALREADY_USED(1024, "Email đã được sử dụng", HttpStatus.BAD_REQUEST),
	CAN_NOT_REQUEST(1025, "Không thể đăng ký khóa học", HttpStatus.BAD_REQUEST),
	TOKEN_INVALID(1026, "Token không hợp lệ", HttpStatus.BAD_REQUEST),
	INVALID_IMAGE_TYPE(1027, "Định dạng ảnh không hợp lệ", HttpStatus.BAD_REQUEST),
	INVALID_VIDEO_TYPE(1028, "Định dạng video không hợp lệ", HttpStatus.BAD_REQUEST),
	INVALID_FILE_TYPE(1029, "Định dạng file không hợp lệ", HttpStatus.BAD_REQUEST),
	UNSUPPORTED_FILE_TYPE(1030, "Loại file không hỗ trợ", HttpStatus.BAD_REQUEST),
	GROUP_NOT_FOUND(1031, "Không tìm thấy nhóm", HttpStatus.NOT_FOUND),
	POST_NOT_FOUND(1022, "Không tìm thấy bài đăng", HttpStatus.NOT_FOUND),
	COMMENT_NOT_FOUND(1023, "Không tìm thấy bình luận", HttpStatus.NOT_FOUND),
	OWNER_NOT_MATCH(1024, "Bạn không phải chủ nhân bình luận. Không thể sửa", HttpStatus.INTERNAL_SERVER_ERROR),
	QUESTION_NOT_FOUND(1025, "Không tìm thấy câu hỏi", HttpStatus.NOT_FOUND),
	TEST_NOT_FOUND(1026, "Không tìm thấy bài kiểm tra", HttpStatus.NOT_FOUND),
	STUDENT_ALREADY_IN_COURSE(1027, "Sinh viên này đã tham gia vào lớp học", HttpStatus.BAD_REQUEST),
	COURSE_NOT_FOUND(1028, "Không tìm thấy khóa học", HttpStatus.NOT_FOUND),
	NOT_ALLOWED_TYPE(1029, "Không hỗ trợ", HttpStatus.BAD_REQUEST),
	TEST_NOT_STARTED_YET(1030, "Test has not started yet", HttpStatus.BAD_REQUEST),
	TEST_IS_EXPIRED(1031, "Test is expired", HttpStatus.BAD_REQUEST),
	TEST_ALREADY_STARTED(1032, "Test already started", HttpStatus.BAD_REQUEST),
	ACCOUNT_NOT_FOUND(1033, "Account not found", HttpStatus.NOT_FOUND),
	CHATBOX_NOT_FOUND(1034, "Chatbox not found", HttpStatus.NOT_FOUND),
	CHATBOX_MEMBER_NOT_FOUND(1035, "No ChatMember in this ChatBox", HttpStatus.NOT_FOUND),
	RESULT_NOT_FOUND(1036, "RESULT_NOT_FOUND", HttpStatus.NOT_FOUND),
	STUDENT_ALREADY_IN_GROUP(1037, "Sinh viên này đã tham gia vào nhóm", HttpStatus.BAD_REQUEST),
	INVALID_PARAMETER(1038, "Danh sách tài khoản không được rỗng", HttpStatus.BAD_REQUEST),
	NOT_NULL(1039, "Không được để trống", HttpStatus.BAD_REQUEST),
	FILE_NOT_FOUND(1040, "Không tìm thấy file", HttpStatus.NOT_FOUND),
	STATUS_NOT_ALLOWED(1041, "Status không hợp lệ", HttpStatus.BAD_REQUEST),
	COURSE_ENDED(1042, "Khóa học đã kết thúc", HttpStatus.BAD_REQUEST),
	COURSE_NOT_STARTED(1043, "Khóa học chưa bắt đầu", HttpStatus.BAD_REQUEST),
	FILE_UPLOAD_FAILED(1044, "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
	DOCUMENT_NOT_FOUND(1045, "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_OPERATION(1046, "Invalid operation performed", HttpStatus.BAD_REQUEST),
	MEMBER_NOT_FOUND_IN_CHATBOX(1047, "Thành viên không tìm thấy trong nhóm chat", HttpStatus.NOT_FOUND),
	PAYMENT_NOT_FOUND(1048, "Không tìm thấy thông tin thanh toán", HttpStatus.NOT_FOUND);


	private ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.Code = code;
		this.Message = message;
		this.statusCode = statusCode;
	}

	private int Code;
	private String Message;
	private HttpStatusCode statusCode;

}
