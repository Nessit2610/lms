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
	private final String[] PUBLIC_API = {"/account/changePassword","/auth/token","/auth/introspect","/auth/logout","/auth/refresh","/student/create","/teacher/create"};
	
	private final String[] PUBLIC_GET_API = {"/test","/major"};
	
	private final String[] PUBLIC_API_V2 = {"/email/**","/paypal/success","/paypal/cancel"};
	
	
	private final String[] TEACHER_POST_API = {"/course/create","/lesson/create","/group/create"};

	private final String[] ADMIN_GET_API= {"/account"};

	@Autowired
	private CustomJwtDecoder customJwtDecoder;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_API).permitAll()
				.requestMatchers(HttpMethod.GET, PUBLIC_GET_API).permitAll()
				.requestMatchers(PUBLIC_WS_API).permitAll()
				.requestMatchers(PUBLIC_API_V2).permitAll()
				.requestMatchers(HttpMethod.POST, TEACHER_POST_API).hasAnyRole(Roles.ADMIN.name(), Roles.TEACHER.name())
				.requestMatchers(HttpMethod.GET, ADMIN_GET_API).hasRole(Roles.ADMIN.name())
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
