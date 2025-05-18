package com.husc.lms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
	private double price;
    private String currency;
    private String method;
    private String intent;
    private String description;

}
