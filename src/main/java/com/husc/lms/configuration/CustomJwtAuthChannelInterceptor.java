package com.husc.lms.configuration;

import com.husc.lms.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("customJwtAuthChannelInterceptor")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomJwtAuthChannelInterceptor implements ChannelInterceptor {
        private static final Logger logger = LoggerFactory.getLogger(CustomJwtAuthChannelInterceptor.class);
        private final CustomJwtDecoder customJwtDecoder;

        // Store authenticated sessions
        public static final Map<String, UsernamePasswordAuthenticationToken> authenticatedSessions = new ConcurrentHashMap<>();

        @Autowired
        public CustomJwtAuthChannelInterceptor(CustomJwtDecoder customJwtDecoder) {
                this.customJwtDecoder = customJwtDecoder;
        }

        @Override
        public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                logger.info("[JWT WS Auth Interceptor] !!!!! ENTERING preSend !!!!!");

                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionId = accessor.getSessionId();
                StompCommand command = accessor.getCommand();

                // Log current state
                logger.debug("[JWT WS Auth Interceptor] Current state - SessionId: {}, Command: {}, HasStoredAuth: {}, User: {}",
                                sessionId, command,
                                (sessionId != null && authenticatedSessions.containsKey(sessionId)),
                                (accessor.getUser() != null ? accessor.getUser().getName() : "null"));

                // Handle CONNECT command with new token
                if (StompCommand.CONNECT.equals(command)) {
                        String authHeader = accessor.getFirstNativeHeader("Authorization");
                        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
                                // Clean up any existing session with the same sessionId
                                if (sessionId != null) {
                                        authenticatedSessions.remove(sessionId);
                                        logger.info("[JWT WS Auth Interceptor] Cleaned up old session {} for reconnection",
                                                        sessionId);
                                }
                        }
                }

                // Check if we have a stored authentication for this session
                if (sessionId != null && authenticatedSessions.containsKey(sessionId)) {
                        UsernamePasswordAuthenticationToken existingAuth = authenticatedSessions.get(sessionId);
                        accessor.setUser(existingAuth);
                        logger.info("[JWT WS Auth Interceptor] Using existing authentication for session {}: user={}, command={}",
                                        sessionId, existingAuth.getName(), command);
                        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                }

                String authHeader = accessor.getFirstNativeHeader("Authorization");
                logger.info("[JWT WS Auth Interceptor] preSend method invoked. Command: {}, SessionId: {}, User: {}, AuthorizationHeaderPresent: {}",
                                command, sessionId,
                                (accessor.getUser() != null ? accessor.getUser().getName() : "null"),
                                (authHeader != null && !authHeader.isEmpty()));

                // Handle authentication for all commands
                if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
                        String jwtString = authHeader.substring(7);
                        logger.debug("[JWT WS Auth Interceptor] Extracted JWT: {}", jwtString);
                        try {
                                Jwt jwt = customJwtDecoder.decode(jwtString);
                                String username = jwt.getSubject();

                                if (username == null || username.trim().isEmpty()) {
                                        logger.warn("[JWT WS Auth Interceptor] JWT 'sub' claim is null or empty. Cannot create Authentication.");
                                } else {
                                        Collection<GrantedAuthority> authorities = new ArrayList<>();
                                        String scopeClaim = jwt.getClaimAsString("scope");
                                        if (scopeClaim != null && !scopeClaim.trim().isEmpty()) {
                                                String[] scopes = scopeClaim.split(" ");
                                                for (String scope : scopes) {
                                                        if (scope.trim().length() > 0) {
                                                                authorities.add(new SimpleGrantedAuthority(
                                                                                scope.trim()));
                                                        }
                                                }
                                                logger.debug("[JWT WS Auth Interceptor] Granted authorities from scope for user '{}': {}",
                                                                username, authorities);
                                        } else {
                                                logger.warn("[JWT WS Auth Interceptor] 'scope' claim is missing or empty in JWT for user '{}'. No authorities will be set.",
                                                                username);
                                        }

                                        UsernamePasswordAuthenticationToken authenticatedPrincipal = new UsernamePasswordAuthenticationToken(
                                                        username, null, authorities);

                                        accessor.setUser(authenticatedPrincipal);

                                        // Store the authentication for this session
                                        if (sessionId != null) {
                                                authenticatedSessions.put(sessionId, authenticatedPrincipal);
                                                logger.info("[JWT WS Auth Interceptor] Stored authentication for session {}: user={}",
                                                                sessionId, username);
                                        }

                                        logger.info("[JWT WS Auth Interceptor] Successfully authenticated user '{}' with authorities: {}. WebSocket session '{}'",
                                                        username, authorities, sessionId);
                                        System.out.printf(
                                                        "[WS-SYS-AUTH] User '%s' authenticated for WebSocket session %s%n",
                                                        username, sessionId);

                                        return MessageBuilder.createMessage(message.getPayload(),
                                                        accessor.getMessageHeaders());
                                }
                        } catch (AppException appEx) {
                                logger.error("[JWT WS Auth Interceptor] AppException during JWT validation: {}. Token: {}",
                                                appEx.getErrorCode().getMessage(), jwtString, appEx);
                                System.out.printf("[WS-SYS-AUTH-FAIL] AppException for session %s: %s%n",
                                                sessionId, appEx.getErrorCode().getMessage());
                        } catch (JwtException ex) {
                                logger.error("[JWT WS Auth Interceptor] JWT validation failed: {}. Token: {}",
                                                ex.getMessage(), jwtString, ex);
                                System.out.printf("[WS-SYS-AUTH-FAIL] JWT validation failed for session %s: %s%n",
                                                sessionId, ex.getMessage());
                        } catch (Exception e) {
                                logger.error("[JWT WS Auth Interceptor] Generic exception during JWT processing: {}. Token: {}",
                                                e.getMessage(), jwtString, e);
                                System.out.printf("[WS-SYS-AUTH-FAIL] Generic exception for session %s: %s%n",
                                                sessionId, e.getMessage());
                        }
                } else {
                        logger.warn("[JWT WS Auth Interceptor] WebSocket {} without 'Authorization: Bearer <token>' header for session {}",
                                        command, sessionId);
                        System.out.printf(
                                        "[WS-SYS-AUTH-WARN] WebSocket {} without Authorization Bearer token for session %s%n",
                                        command, sessionId);
                }

                // For DISCONNECT command, clean up the session
                if (StompCommand.DISCONNECT.equals(command) && sessionId != null) {
                        authenticatedSessions.remove(sessionId);
                        logger.info("[JWT WS Auth Interceptor] Removed authentication for session {}", sessionId);
                }

                // If we have a session but no authentication, try to get it from the stored
                // sessions
                if (sessionId != null && accessor.getUser() == null && authenticatedSessions.containsKey(sessionId)) {
                        UsernamePasswordAuthenticationToken storedAuth = authenticatedSessions.get(sessionId);
                        accessor.setUser(storedAuth);
                        logger.info("[JWT WS Auth Interceptor] Restored authentication for session {}: user={}, command={}",
                                        sessionId, storedAuth.getName(), command);
                        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                }

                // Log final state before returning
                logger.debug("[JWT WS Auth Interceptor] Final state - SessionId: {}, Command: {}, User: {}",
                                sessionId, command,
                                (accessor.getUser() != null ? accessor.getUser().getName() : "null"));

                return message;
        }
}