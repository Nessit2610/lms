package com.husc.lms.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cấu hình prefix cho message để send đến (ví dụ: /app)
        config.setApplicationDestinationPrefixes("/app");
        // Cấu hình prefix cho message để subscribe (ví dụ: /topic)
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
