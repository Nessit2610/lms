package com.husc.lms.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
	
	public String message() default "Invalid date of birth";
	
	public int min();
	
	public Class<?>[] groups() default {};
	
	public Class<? extends Payload>[] payload() default {}; 
}
