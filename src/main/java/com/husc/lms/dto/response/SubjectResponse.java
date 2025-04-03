package com.husc.lms.dto.response;

import java.util.List;

import com.husc.lms.entity.CurriculumSubject;
import com.husc.lms.entity.SubjectMaterial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponse {

	private String id;

    private String code;

    private String name;

    private String status;
    
    private List<SubjectMaterialResponse> subjectMaterials;
    
    //private List<CurriculumSubject> curriculumSubjects;
}
