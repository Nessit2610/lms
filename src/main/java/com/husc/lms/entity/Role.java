package com.husc.lms.entity;

import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lms_role")
public class Role {

    @Id
    private String name; 
    private String description;

    @ManyToMany
    private Set<Permission> permission;
}
