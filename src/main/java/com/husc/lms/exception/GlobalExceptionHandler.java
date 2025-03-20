package com.husc.lms.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.enums.ErrorCode;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	
	private static final String MIN_ATTRIBUTES = "min";
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<APIResponse> handlingRuntimeException(Exception exception){
		APIResponse apiResponse = new APIResponse();
		apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
		apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	} 	
	
	
	
	@ExceptionHandler(value = AppException.class)
	public ResponseEntity<APIResponse> handlingAppException(AppException exception){
		
		ErrorCode errorCode = exception.getErrorCode();
		
		APIResponse apiResponse = new APIResponse();
		
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
	}
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<APIResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exeption){
		
		String enumkey = exeption.getFieldError().getDefaultMessage();
		
		ErrorCode errorCode = ErrorCode.INVALID_KEY;
		Map<String, Object> attributes = null;
		try {
			errorCode = ErrorCode.valueOf(enumkey);
			
			var constraintViolation = exeption.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
			
			attributes = constraintViolation.getConstraintDescriptor().getAttributes();
			log.info(attributes.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		APIResponse apiResponse = new APIResponse();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(Objects.nonNull(attributes)? mapAttributes(errorCode.getMessage(), attributes) : errorCode.getMessage());
		
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	
	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<APIResponse> handlingAccessDeniedException(AccessDeniedException exception){
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
		return ResponseEntity.status(errorCode.getStatusCode())
				.body(APIResponse.builder()
						.code(errorCode.getCode())
						.message(errorCode.getMessage())
						.build());
	}
	@ExceptionHandler(value = JwtException.class)
	public ResponseEntity<APIResponse> handleJwtException(JwtException exception) {
	    ErrorCode errorCode = ErrorCode.UNAUTHORIZED; 
	    return ResponseEntity.status(errorCode.getStatusCode())
	            .body(APIResponse.builder()
	                    .code(errorCode.getCode())
	                    .message("Token không hợp lệ hoặc đã hết hạn")
	                    .build());
	}

	
	@ExceptionHandler(value = NoHandlerFoundException.class)
	public ResponseEntity<APIResponse> handlingNoHandlerFoundException(NoHandlerFoundException exception){
		ErrorCode errorCode = ErrorCode.NOTFOUND_API;
		APIResponse apiResponse = new APIResponse();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	
	private String mapAttributes(String message, Map<String, Object> attributes) {
		String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTES));
		return message.replace("{" + MIN_ATTRIBUTES + "}", minValue);
	}
}
