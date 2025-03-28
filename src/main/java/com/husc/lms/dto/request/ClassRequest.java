package com.husc.lms.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRequest {

    private String code;

    private String status;

    private String name;

    private String teacherId;

    private String courseId;
}