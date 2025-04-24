package com.husc.lms.configuration;

import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.husc.lms.constant.PredefinedRole;
import com.husc.lms.entity.Role;
import com.husc.lms.entity.Account;
import com.husc.lms.repository.RoleRepository;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (accountRepository.findByUsernameAndDeletedDateIsNull(ADMIN_USER_NAME).isEmpty()) {
            	
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.STUDENT_ROLE)
                        .description("Student role")
                        .build());
                roleRepository.save(Role.builder()
                		.name(PredefinedRole.TEACHER_ROLE)
                		.description("Teacher role")
                		.build());
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                Account account = Account.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .active(true)
                        .createdBy(ADMIN_USER_NAME)
                        .createdDate(new Date())
                        .roles(roles)
                        .build();

                accountRepository.save(account);
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
