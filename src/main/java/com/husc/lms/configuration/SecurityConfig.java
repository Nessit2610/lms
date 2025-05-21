package com.husc.lms.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import com.husc.lms.enums.Roles;

@Configuration
@EnableWebSecurity
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class SecurityConfig {
	private final String[] PUBLIC_WS_API = {
		    "/ws/**"
		};	
	private final String[] PUBLIC_API = {"/auth/**","/email/**","/major"};
	
	private final String[] TEACHER_POST_API = {
		    "/chapter/create",
		    "/course/create",
		    "/course/{id}/upload-photo",
		    "/document/create",
		    "/group/create",
		    "/joinclass/approved",
		    "/joinclass/rejected",
		    "/lesson/create",
		    "/lessonmaterial/create",
		    "/lessonquiz/{idLesson}/create",
		    "/post/create",
		    "/studentcourse/addstudents",
		    "/studentgroup/addstudent",
		    "/testingroup/create"
		};

		private final String[] TEACHER_GET_API = {
		    "/course/courseofteacher",
		    "/document/mydocument",
		    "/group/groupofteacher",
		    "/group/searchgroupofteacher",
		    "/joinclass/studentrequest",
		    "/student/searchnotingroup",
		    "/studentcourse/searchstudent",
		    "/studentcourse/searchstudentnotin",
		    "/studentgroup/getstudent",
		    "/studentgroup/searchstudent",
		    "/teststudentresult/gettestresult",
		    "/teststudentresult/getallresult"
		};

		private final String[] TEACHER_PUT_API = {
		    "/chapter/update",
		    "/course/update",
		    "/document/updatestatus",
		    "/group/update",
		    "/lesson/update",
		    "/post/update",
		    "/testingroup/update"
		};

		private final String[] TEACHER_DELETE_API = {
		    "/chapter/delete",
		    "/course/delete",
		    "/document/delete",
		    "/document/deleteall",
		    "/lesson/{lessonId}",
		    "/lessonmaterial/{lessonMaterialId}",
		    "/lessonquiz/{lessonQuizId}",
		    "/post/delete",
		    "/studentcourse/delete",
		    "/studentcourse/deleteall",
		    "/studentgroup/delete",
		    "/studentgroup/deleteall",
		    "/testingroup/delete"
		};

		private final String[] STUDENT_POST_API = {
			    "/joinclass/pending",
			    "/paypal/pay",
			    "/student/{id}/upload-photo",
			    "/lessonchapterprogress/savechapterprogress/{chapterId}",
			    "/lessonchapterprogress/completechapter/{chapterId}",
			    "/lessonprogress/savelessonprogress/{lessonId}",
			    "/teststudentresult/starttest",
			    "/teststudentresult/submitTest"
			};
		private final String[] STUDENT_GET_API = {
			    "/course/courseofmajorfirst",
			    "/joinclass/courserequest",
			    "/paypal/success",
			    "/paypal/cancel",
			    "/studentcourse/mycourse",
			    "/studentgroup/getgroup",
			    "/studentgroup/searchgroupofstudent",
			    "/lessonchapterprogress/getprogress/{chapterId}",
			    "/lessonprogress/getprogress/{lessonId}",
			    "/teststudentresult/gettestdetail"
			};
		private final String[] STUDENT_PUT_API = {
			    "/lessonchapterprogress/completechapter/{chapterId}",
			    "/lessonprogress/completelesson/{lessonId}"
			};

		private final String[] STUDENT_DELETE_API = {
			    // Không có API DELETE cho STUDENT 
			};

		private final String[] ADMIN_POST_API = {
			    "/major/create",
			    "/permission/create",
			    "/roles/create",
			    "/student/create",
			    "/teacher/create"
			};
		private final String[] ADMIN_GET_API = {
			    "/account",
			    "/account/{accountId}",
			    "/account/details/{accountId}",
			    "/course/search",
			    "/permission",
			    "/roles",
			    "/student/search",
			    "/teacher"
			};
		private final String[] ADMIN_PUT_API = {
			    "/account/changeactive",
			    "/roles/update"
			};
		private final String[] ADMIN_DELETE_API = {
			    "/major/delete",
			    "/permission/delete",
			    "/roles/delete"
			};



	

	@Autowired
	private CustomJwtDecoder customJwtDecoder;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_API).permitAll()
				.requestMatchers(PUBLIC_WS_API).permitAll()
				.requestMatchers(PUBLIC_API).permitAll()
				.requestMatchers(HttpMethod.GET, TEACHER_GET_API).hasRole(Roles.TEACHER.name())
				.requestMatchers(HttpMethod.POST, TEACHER_POST_API).hasRole(Roles.TEACHER.name())
				.requestMatchers(HttpMethod.PUT, TEACHER_PUT_API).hasRole(Roles.TEACHER.name())
				.requestMatchers(HttpMethod.DELETE, TEACHER_DELETE_API).hasRole(Roles.TEACHER.name())
				.requestMatchers(HttpMethod.GET, STUDENT_GET_API).hasRole(Roles.STUDENT.name())
				.requestMatchers(HttpMethod.POST, STUDENT_POST_API).hasRole(Roles.STUDENT.name())
				.requestMatchers(HttpMethod.PUT, STUDENT_PUT_API).hasRole(Roles.STUDENT.name())
				.requestMatchers(HttpMethod.DELETE, STUDENT_DELETE_API).hasRole(Roles.STUDENT.name())
				.requestMatchers(HttpMethod.GET, ADMIN_GET_API).hasRole(Roles.ADMIN.name())
				.requestMatchers(HttpMethod.POST, ADMIN_POST_API).hasRole(Roles.ADMIN.name())
				.requestMatchers(HttpMethod.PUT, ADMIN_PUT_API).hasRole(Roles.ADMIN.name())
				.requestMatchers(HttpMethod.DELETE, ADMIN_DELETE_API).hasRole(Roles.ADMIN.name())
				.anyRequest().authenticated());

		httpSecurity.oauth2ResourceServer(
				oauth2configure -> oauth2configure.jwt(jwtconfigure -> jwtconfigure.decoder(customJwtDecoder)
						.jwtAuthenticationConverter(jwtAuthenticationConverter())));

		httpSecurity.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:3000"));
			config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
			config.setAllowedHeaders(List.of("*"));
			config.setAllowCredentials(true);
			return config;
		}));
		httpSecurity.csrf(crfsconfigure -> crfsconfigure
				.ignoringRequestMatchers("/ws/**")
				.disable());
		return httpSecurity.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {

		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
