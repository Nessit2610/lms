package com.husc.lms.dto.response;


import java.util.List;

import com.husc.lms.entity.LearningPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPlanOfStudent {

	private String code;

    private String fullName;

    private String firstName;

    private String lastName;
    
    private List<LearningPlanResponse> learningPlans;
}
