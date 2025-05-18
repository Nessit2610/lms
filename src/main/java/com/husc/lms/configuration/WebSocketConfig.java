package com.husc.lms.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Bean
    public ChannelInterceptor loggingChannelInterceptor() {
        return new ChannelInterceptor() {
            private final Logger interceptorLogger = LoggerFactory.getLogger("LoggingChannelInterceptor");

            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionId = accessor.getSessionId();
                String commandText = (accessor.getCommand() != null) ? accessor.getCommand().name() : "UNKNOWN_COMMAND";
                String destination = accessor.getDestination();
                Object payload = message.getPayload();
                String payloadString = payload.toString();
                String shortPayload = payloadString.substring(0, Math.min(payloadString.length(), 200))
                        + (payloadString.length() > 200 ? "..." : "");

                interceptorLogger.info(
                        "[WS-INTERCEPTOR PRE-SEND] SessionId: {}, Command: {}, Destination: {}, Payload: {}",
                        sessionId, commandText, destination, shortPayload);

                System.out.printf(
                        "[WS-SYS-PRE-SEND] SessionId: %s, Command: %s, Destination: %s, Channel: %s, Payload: %s%n",
                        sessionId, commandText, destination, channel.toString(), shortPayload);
                return message;
            }

            @Override
            public void postSend(@NonNull Message<?> message, @NonNull MessageChannel channel, boolean sent) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionId = accessor.getSessionId();
                String commandText = (accessor.getCommand() != null) ? accessor.getCommand().name() : "UNKNOWN_COMMAND";
                String destination = accessor.getDestination();
                interceptorLogger.info(
                        "[WS-INTERCEPTOR POST-SEND] SessionId: {}, Command: {}, Destination: {}, Sent: {}",
                        sessionId, commandText, destination, sent);
                System.out.printf("[WS-SYS-POST-SEND] SessionId: %s, Command: %s, Destination: %s, Sent: %s%n",
                        sessionId, commandText, destination, sent);
            }
        };
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(loggingChannelInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(loggingChannelInterceptor());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userPrincipalName = (headerAccessor.getUser() != null) ? headerAccessor.getUser().getName()
                : "ANONYMOUS";

        logger.info("[WS-EVENT] CONNECTED - SessionId: {}, User: {}", sessionId, userPrincipalName);
        System.out.printf("[WS-SYS-EVENT] CONNECTED - SessionId: %s, User: %s%n", sessionId, userPrincipalName);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userPrincipalName = (headerAccessor.getUser() != null) ? headerAccessor.getUser().getName()
                : "ANONYMOUS";

        logger.info("[WS-EVENT] DISCONNECTED - SessionId: {}, User: {}", sessionId, userPrincipalName);
        System.out.printf("[WS-SYS-EVENT] DISCONNECTED - SessionId: %s, User: %s%n", sessionId, userPrincipalName);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        String userPrincipalName = (headerAccessor.getUser() != null) ? headerAccessor.getUser().getName()
                : "ANONYMOUS";

        logger.info("[WS-EVENT] SUBSCRIBED - SessionId: {}, User: {}, Destination: {}", sessionId, userPrincipalName,
                destination);
        System.out.printf("[WS-SYS-EVENT] SUBSCRIBED - SessionId: %s, User: %s, Destination: %s%n",
                sessionId, userPrincipalName, destination);
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();
        String userPrincipalName = (headerAccessor.getUser() != null) ? headerAccessor.getUser().getName()
                : "ANONYMOUS";

        logger.info("[WS-EVENT] UNSUBSCRIBED - SessionId: {}, User: {}, SubscriptionId: {}", sessionId,
                userPrincipalName, subscriptionId);
        System.out.printf("[WS-SYS-EVENT] UNSUBSCRIBED - SessionId: %s, User: %s, SubscriptionId: %s%n",
                sessionId, userPrincipalName, subscriptionId);
    }
}
