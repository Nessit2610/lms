package com.husc.lms.exception;

import com.husc.lms.enums.ErrorCode;

public class AppException extends RuntimeException {

    private ErrorCode errorCode;
    private Object detail; // Cho phép đính kèm thông tin phụ (vd: List<String>, Map,...)

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, Object detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }
}
