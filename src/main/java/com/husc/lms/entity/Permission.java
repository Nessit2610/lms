package com.husc.lms.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lms_permission")
public class Permission {

    @Id
    private String name; 
    private String description;

}
