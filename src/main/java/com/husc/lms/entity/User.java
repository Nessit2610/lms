package com.husc.lms.entity;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lms_user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private int version;
    private String username;
    private String password;
    private String email;
    private boolean active;
    private String timeZoneId;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private String deletedBy;
    private Date deletedDate;

    @ManyToMany
    private Set<Role> roles;
}
