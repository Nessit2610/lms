package com.husc.lms.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

// Kích hoạt lại các import cho Security
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity; // KÍCH HOẠT LẠI
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity // KÍCH HOẠT LẠI
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

        @Autowired
        private CustomJwtAuthChannelInterceptor customJwtAuthChannelInterceptor;

        @Bean
        public TaskScheduler heartBeatScheduler() {
                return new ThreadPoolTaskScheduler();
        }

        @Bean
        public ChannelInterceptor loggingChannelInterceptor() {
                return new ChannelInterceptor() {
                        private final Logger interceptorLogger = LoggerFactory.getLogger("LoggingChannelInterceptor");

                        @Override
                        public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                                StompHeaderAccessor loggingAccessor = StompHeaderAccessor.wrap(message);
                                String sessionId = loggingAccessor.getSessionId();
                                String commandText = (loggingAccessor.getCommand() != null)
                                                ? loggingAccessor.getCommand().name()
                                                : "UNKNOWN_COMMAND";
                                String destination = loggingAccessor.getDestination();
                                Object payload = message.getPayload();
                                String payloadString = payload.toString();
                                String shortPayload = payloadString.substring(0, Math.min(payloadString.length(), 200))
                                                + (payloadString.length() > 200 ? "..." : "");

                                String username = "USER_IS_NULL_IN_LOGGING_INTERCEPTOR (SECURITY ENABLED)";
                                if (loggingAccessor.getUser() != null) {
                                        username = loggingAccessor.getUser().getName();
                                        if (username == null) {
                                                username = "USER_PRINCIPAL_NAME_IS_NULL_IN_LOGGING_INTERCEPTOR (SECURITY ENABLED)";
                                        }
                                }

                                // Log thông tin chi tiết hơn về trạng thái xác thực
//                                logger.debug("[LoggingChannelInterceptor] Authentication state - SessionId: {}, User: {}, Command: {}, Destination: {}",
//                                                sessionId, username, commandText, destination);
//
//                                System.out.printf(
//                                                "[WS-SYS-LOGGING PRE-SEND (SECURITY ENABLED)] SessionId: %s, User: %s, Command: %s, Destination: %s, Channel: %s, Payload: %s%n",
//                                                sessionId, username, commandText, destination, channel.toString(),
//                                                shortPayload);
                                return message;
                        }
                };
        }

        @Bean
        public ChannelInterceptor csrfChannelInterceptor() {
                return new ChannelInterceptor() {
                        @Override
                        public Message<?> preSend(Message<?> message, MessageChannel channel) {
                                return message; // Bỏ qua CSRF check
                        }
                };
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
                // Thêm heartbeat 1 ngày
                config.enableSimpleBroker("/topic", "/queue", "/user")
                                .setTaskScheduler(heartBeatScheduler())
                                .setHeartbeatValue(new long[] { 86400000, 86400000 }); // 1 ngày = 86400000ms
                config.setApplicationDestinationPrefixes("/app");
                config.setUserDestinationPrefix("/user");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
                registry.addEndpoint("/ws")
                                .setAllowedOriginPatterns("*")
                                .withSockJS()
                                .setDisconnectDelay(7 * 24 * 60 * 60 * 1000) // 1 tuần = 604800000ms
                                .setHeartbeatTime(24 * 60 * 60 * 1000) // 1 ngày = 86400000ms
                                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js")
                                .setWebSocketEnabled(true)
                                .setSessionCookieNeeded(false);
        }

        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
                registration.interceptors(customJwtAuthChannelInterceptor);
                registration.interceptors(loggingChannelInterceptor());
                registration.interceptors(csrfChannelInterceptor());
        }

        @Override
        public void configureClientOutboundChannel(ChannelRegistration registration) {
                registration.interceptors(loggingChannelInterceptor());
        }

        private String getUserPrincipalName(StompHeaderAccessor headerAccessor) {
                String sessionId = headerAccessor.getSessionId();
                if (headerAccessor.getUser() != null && headerAccessor.getUser().getName() != null) {
                        String username = headerAccessor.getUser().getName();
//                        logger.debug("[WebSocketConfig] Found authenticated user from accessor: {} for session: {}",
//                                        username, sessionId);
                        return username;
                }
                // Tra cứu từ interceptor nếu không có trong accessor
                if (sessionId != null
                                && com.husc.lms.configuration.CustomJwtAuthChannelInterceptor.authenticatedSessions
                                                .containsKey(sessionId)) {
                        String username = com.husc.lms.configuration.CustomJwtAuthChannelInterceptor.authenticatedSessions
                                        .get(sessionId).getName();
//                        logger.debug("[WebSocketConfig] Found authenticated user from interceptor map: {} for session: {}",
//                                        username, sessionId);
                        return username;
                }
//                logger.debug("[WebSocketConfig] No authenticated user found for session: {}", sessionId);
                return null;
        }

        @EventListener
        public void handleWebSocketConnectListener(SessionConnectedEvent event) {
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
                String sessionId = headerAccessor.getSessionId();
                String userPrincipalName = getUserPrincipalName(headerAccessor);

                if (userPrincipalName == null) {
                        userPrincipalName = "ANONYMOUS_OR_PENDING_AUTH (SECURITY ENABLED)";
                }

//                logger.info("[WS-EVENT] CONNECTED (SECURITY ENABLED) - SessionId: {}, User: {}", sessionId,
//                                userPrincipalName);
//                System.out.printf("[WS-SYS-EVENT] CONNECTED (SECURITY ENABLED) - SessionId: %s, User: %s%n", sessionId,
//                                userPrincipalName);
        }

        @EventListener
        public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
                String sessionId = headerAccessor.getSessionId();
                String userPrincipalName = getUserPrincipalName(headerAccessor);
                String closeStatus = "DISCONNECTED"; // Đơn giản hóa, chỉ ghi nhận là đã disconnect

                if (userPrincipalName == null) {
                        userPrincipalName = "ANONYMOUS (SECURITY ENABLED)";
                }

//                logger.info("[WS-EVENT] DISCONNECTED (SECURITY ENABLED) - SessionId: {}, User: {}, CloseStatus: {}",
//                                sessionId, userPrincipalName, closeStatus);
//                System.out.printf(
//                                "[WS-SYS-EVENT] DISCONNECTED (SECURITY ENABLED) - SessionId: %s, User: %s, CloseStatus: %s%n",
//                                sessionId, userPrincipalName, closeStatus);
        }

        @EventListener
        public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
                String sessionId = headerAccessor.getSessionId();
                String destination = headerAccessor.getDestination();
                String userPrincipalName = getUserPrincipalName(headerAccessor);

                if (userPrincipalName == null) {
                        userPrincipalName = "ANONYMOUS_SUBSCRIBE_ATTEMPT (SECURITY ENABLED)";
//                        logger.warn("[WebSocketConfig] Anonymous subscribe attempt for session: {}, destination: {}",
//                                        sessionId, destination);
                } else {
//                        logger.info("[WebSocketConfig] Authenticated subscribe for user: {}, session: {}, destination: {}",
//                                        userPrincipalName, sessionId, destination);
                }

//                logger.info("[WS-EVENT] SUBSCRIBED (SECURITY ENABLED) - SessionId: {}, User: {}, Destination: {}",
//                                sessionId, userPrincipalName, destination);
//                System.out.printf(
//                                "[WS-SYS-EVENT] SUBSCRIBED (SECURITY ENABLED) - SessionId: %s, User: %s, Destination: %s%n",
//                                sessionId, userPrincipalName, destination);
        }

        @EventListener
        public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
                String sessionId = headerAccessor.getSessionId();
                String subscriptionId = headerAccessor.getSubscriptionId();
                String userPrincipalName = getUserPrincipalName(headerAccessor);

                if (userPrincipalName == null) {
                        userPrincipalName = "ANONYMOUS (SECURITY ENABLED)";
                }

//                logger.info("[WS-EVENT] UNSUBSCRIBED (SECURITY ENABLED) - SessionId: {}, User: {}, SubscriptionId: {}",
//                                sessionId, userPrincipalName, subscriptionId);
//                System.out.printf(
//                                "[WS-SYS-EVENT] UNSUBSCRIBED (SECURITY ENABLED) - SessionId: %s, User: %s, SubscriptionId: %s%n",
//                                sessionId, userPrincipalName, subscriptionId);
        }

        @Bean
        public AuthorizationManager<Message<?>> messageAuthorizationManager(
                        MessageMatcherDelegatingAuthorizationManager.Builder messages) {
                return messages
                                .simpDestMatchers("/ws/**").permitAll()
                                .simpSubscribeDestMatchers("/topic/**", "/queue/**", "/user/**").permitAll()
                                .simpMessageDestMatchers("/app/**").permitAll()
                                .simpTypeMatchers(SimpMessageType.CONNECT,
                                                SimpMessageType.CONNECT_ACK,
                                                SimpMessageType.DISCONNECT,
                                                SimpMessageType.DISCONNECT_ACK,
                                                SimpMessageType.HEARTBEAT,
                                                SimpMessageType.MESSAGE)
                                .permitAll()
                                .anyMessage().permitAll()
                                .build();
        }
}