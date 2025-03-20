package com.husc.lms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")  
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMongo {

    @Id 
    private String id;
    
    private String name;
    private String email;

}
