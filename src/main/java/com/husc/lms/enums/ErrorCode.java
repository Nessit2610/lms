package com.husc.lms.enums;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
	
	NOTFOUND_API(404,"API not exist",HttpStatus.BAD_REQUEST),
	USER_EXISTED(1001,"User Existed",HttpStatus.BAD_REQUEST),
	UNCATEGORIZED_EXCEPTION(1002,"Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
	USERNAME_INVALID(1003, "Username must be at least {min} characters",HttpStatus.BAD_REQUEST),
	PASSWORD_INVALID(1004,"Password must be at least {min} characters",HttpStatus.BAD_REQUEST),
	INVALID_KEY(1005,"Invalid message key",HttpStatus.BAD_REQUEST),
	USER_NOTFOUND(1006,"User not found",HttpStatus.BAD_REQUEST),
	USER_UNAUTHENTICATED(1007, "Unauthenticated",HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1008, "You do not have permission",HttpStatus.FORBIDDEN),
	ROLE_NOT_FOUND(1009, "Role not found", HttpStatus.BAD_REQUEST),
	OLD_PASSWORD_NOT(1009, "Mat Khau cu sai cmnr", HttpStatus.BAD_REQUEST),
	MAJOR_NOT_FOUND(1009, "Tim deo thay Major", HttpStatus.BAD_REQUEST),
	CODE_ERROR(1009, "Loi chua dinh dang", HttpStatus.BAD_REQUEST),
	DOB_INVALID(1010, "Your age must be at least {min}", HttpStatus.BAD_REQUEST);
	
	
	private ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.Code = code;
		this.Message = message;
		this.statusCode = statusCode;
	}

	private int Code;
	private String Message;
	private HttpStatusCode statusCode;
	
	
}
