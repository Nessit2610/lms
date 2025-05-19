package com.husc.lms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

	@NotNull(message = "NOT_NULL")
	private Double price;
	@NotNull(message = "NOT_NULL")
    private String currency;
	@NotNull(message = "NOT_NULL")
    private String method;
	@NotNull(message = "NOT_NULL")
    private String intent;
	@NotNull(message = "NOT_NULL")
    private String description;
	@NotNull(message = "NOT_NULL")
    private String cancelUrl;
	@NotNull(message = "NOT_NULL")
    private String successUrl;
	@NotNull(message = "NOT_NULL")
    private String courseId;
}
