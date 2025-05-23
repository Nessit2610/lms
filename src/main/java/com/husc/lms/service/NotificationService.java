package com.husc.lms.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.CommentNotificationResponse;
import com.husc.lms.dto.response.NotificationResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Group;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.NotificationRepository;

import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;

import lombok.RequiredArgsConstructor;
import com.husc.lms.service.OffsetLimitPageRequest;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoRepository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LessonRepository lessonRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupRepository groupRepository;

    public CommentNotificationResponse getAllUnreadCommentNotificationOfAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                        .orElseThrow(() -> new RuntimeException("Account not found"));

        // Lấy tất cả notification COMMENT của người dùng
        List<Notification> notifications = notificationRepository
                        .findByAccountAndType(account, NotificationType.COMMENT);

        // Đếm số lượng chưa đọc
        int unreadCount = (int) notifications.stream()
                        .filter(notification -> !notification.isRead())
                        .count();

        return CommentNotificationResponse.builder()
                        .notifications(notifications)
                        .totalCount(notifications.size())
                        .unreadCount(unreadCount)
                        .build();
    }

    public void setNotificationAsReadByAccountWithListNotificationId(List<String> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
                return;
        }
        notificationRepository.setNotificationAsReadByAccountWithListNotificationId(notificationIds);
    }

    public void markCommentNotificationsAsRead(List<NotificationRequest> notificationRequests) {
        if (notificationRequests == null || notificationRequests.isEmpty()) {
                return;
        }
        List<String> notificationIds = notificationRequests.stream()
                        .map(NotificationRequest::getId)
                        .collect(Collectors.toList());
        if (!notificationIds.isEmpty()) {
                notificationRepository.setNotificationAsReadByAccountWithListNotificationId(notificationIds);
        }
    }

    public NotificationResponse getNotificationsByAccount(int offset, int pageSize) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                        .orElseThrow(() -> new RuntimeException("Account not found for username: " + username));

        if (pageSize < 1) {
                throw new IllegalArgumentException("pageSize must be 1 or greater.");
        }
        if (offset < 0) {
                throw new IllegalArgumentException("offset must not be less than zero.");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageableForHasNextCheck = new OffsetLimitPageRequest(offset, pageSize + 1, sort);

        Page<Notification> notificationPage = notificationRepository.findByAccount(account, pageableForHasNextCheck);

        List<Notification> fetchedNotifications = notificationPage.getContent();
        boolean hasNextPage = fetchedNotifications.size() > pageSize;

        List<Notification> notificationsToReturn = hasNextPage
                        ? fetchedNotifications.subList(0, pageSize)
                        : fetchedNotifications;

        List<NotificationResponse.NotificationDetail> notificationDetails = notificationsToReturn
                        .stream()
                        .map(notification -> {
                            String courseId = null, lessonId = null, chapterId = null;
                            if (notification.getComment() != null) {
                                courseId = Optional.ofNullable(notification.getComment().getCourse())
                                                .map(Course::getId).orElse(null);
                                chapterId = Optional.ofNullable(notification.getComment().getChapter())
                                                .map(Chapter::getId).orElse(null);
                                lessonId = Optional.ofNullable(notification.getComment().getChapter())
                                                .map(ch -> lessonRepository.findByChapterAndDeletedDateIsNull(ch))
                                                .map(lesson -> lesson != null ? lesson.getId() : null)
                                                .orElse(null);
                            } else if (notification.getCommentReply() != null) {
                                Comment parentComment = notification.getCommentReply().getComment();
                                if (parentComment != null) {
                                        courseId = Optional.ofNullable(parentComment.getCourse())
                                                        .map(Course::getId).orElse(null);
                                        chapterId = Optional.ofNullable(parentComment.getChapter())
                                                        .map(Chapter::getId).orElse(null);
                                        lessonId = Optional.ofNullable(parentComment.getChapter())
                                                        .map(ch -> lessonRepository.findByChapterAndDeletedDateIsNull(ch))
                                                        .map(lesson -> lesson != null ? lesson.getId()
                                                                        : null)
                                                        .orElse(null);
                                }
                            }

                            String chatBoxId = null;
                            if (notification.getChatMessageId() != null) {
                                ChatMessage chatMessage = chatMessageRepository
                                                .findById(notification.getChatMessageId()).orElse(null);
                                chatBoxId = chatMessage != null ? chatMessage.getChatBoxId() : null;
                            }

                            String postId = Optional.ofNullable(notification.getPost()).map(p -> p.getId())
                                            .orElse(null);

                            String groupId = null;
                            if (postId != null) {
                                groupId = Optional.ofNullable(groupRepository.findByPostId(postId))
                                                .map(Group::getId)
                                                .orElse(null);
                            }

                            return NotificationResponse.NotificationDetail.builder()
                                            .notificationId(notification.getId())
                                            .receivedAccountId(notification.getAccount().getId())
                                            .courseId(courseId)
                                            .lessonId(lessonId)
                                            .chapterId(chapterId)
                                            .chatBoxId(chatBoxId)
                                            .groupId(groupId)
                                            .postId(postId)
                                            .notificationType(notification.getType())
                                            .isRead(notification.isRead())
                                            .description(notification.getDescription())
                                            .createdAt(notification.getCreatedAt())
                                            .build();
                        })
                        .collect(Collectors.toList());

        Integer unreadCount = notificationRepository.countByAccountAndIsReadFalse(account);

        return NotificationResponse.builder()
                        .notificationDetails(notificationDetails)
                        .countUnreadNotification(unreadCount)
                        .build();
    }

    public void sendCustomWebSocketNotificationToUser(String targetUsername, Map<String, Object> payload) {
        if (targetUsername == null || targetUsername.isEmpty()) {
                System.err.println("sendCustomWebSocketNotificationToUser: targetUsername không được rỗng.");
                return;
        }
        if (payload == null || payload.isEmpty()) {
                System.err.println("sendCustomWebSocketNotificationToUser: payload không được rỗng.");
                return;
        }

        String destination = "/topic/notifications/" + targetUsername;
        System.out.println("[NotificationService] Sending WebSocket notification to: " + destination
                        + " with payload: " + payload);
        messagingTemplate.convertAndSend(destination, payload);

    }

    public void sendConstructedCommentReplyNotification(String targetUsername, Account ownerOfParentComment,
                    CommentReply savedReply, Chapter chapter, Course course, Comment parentComment) {

        if (targetUsername == null || targetUsername.isEmpty()) {
            System.err.println(
                            "[NotificationService] sendConstructedCommentReplyNotification: targetUsername không được rỗng.");
            return;
        }
        if (ownerOfParentComment == null || savedReply == null || chapter == null || course == null
                        || parentComment == null) {
            System.err.println(
                            "[NotificationService] sendConstructedCommentReplyNotification: Một trong các tham số để tạo payload là null.");
            return;
        }

        String messageText;
        if (parentComment.getAccount().getId().equals(ownerOfParentComment.getId())) {
            messageText = "Người dùng " + savedReply.getReplyAccount().getUsername()
                            + " đã trả lời bình luận của bạn: " // Changed to replyAccount's username
                            + savedReply.getDetail();
        } else {
            messageText = "Có trả lời mới cho bình luận \"" + parentComment.getDetail() + "\": "
                            + savedReply.getDetail() +
                            " bởi " + savedReply.getReplyAccount().getUsername();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", messageText);
        payload.put("type", NotificationType.COMMENT_REPLY.name());
        payload.put("courseId", course.getId());
        payload.put("lessonId", chapter.getLesson().getId()); // Assuming chapter always has a lesson
        payload.put("chapterId", chapter.getId());
        payload.put("parentCommentId", parentComment.getId());
        payload.put("commentReplyId", savedReply.getId());
        payload.put("createdDate", new Date());
        payload.put("replierUsername", savedReply.getReplyAccount().getUsername());

        System.out.println("[NotificationService] Constructing and sending CommentReplyNotification to: "
                        + targetUsername + " with payload: " + payload);
        this.sendCustomWebSocketNotificationToUser(targetUsername, payload);
    }

    public void setNotificationAsReadByAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                        .orElseThrow(() -> new RuntimeException("Account not found for username: " + username));

        notificationRepository.setNotificationAsReadByAccount(account);
    }

    public void createChatMessageNotificationForChatBoxMembers(ChatMessage chatMessage,
                    List<String> memberUsernames) {
        if (chatMessage == null || memberUsernames == null || memberUsernames.isEmpty())
                return;
        String sender = chatMessage.getSenderAccount();
        // Get sender's account to get their full name
        Account senderAccount = accountRepository.findByUsernameAndDeletedDateIsNull(sender).orElse(null);
        if (senderAccount == null)
                return;
        String senderFullname = senderAccount.getStudent() != null ? senderAccount.getStudent().getFullName()
                        : senderAccount.getTeacher() != null ? senderAccount.getTeacher().getFullName()
                                        : "";
        for (String username : memberUsernames) {
            if (username.equals(sender))
                    continue; // Không gửi cho người gửi
            Account receiver = accountRepository.findByUsernameAndDeletedDateIsNull(username).orElse(null);
            if (receiver == null)
                    continue;
            Notification notification = Notification.builder()
                            .account(receiver)
                            .type(NotificationType.CHAT_MESSAGE.name())
                            .chatMessageId(chatMessage.getId())
                            .isRead(false)
                            .description("Người dùng " + senderFullname + " vừa nhắn: " +
                                            (chatMessage.getContent() != null ? chatMessage.getContent()
                                                            : "[File]"))
                            .createdAt(OffsetDateTime.now())
                            .build();
            notificationRepository.save(notification);
            // Gửi realtime notification
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", NotificationType.CHAT_MESSAGE.name());
            payload.put("notificationId", notification.getId());
            payload.put("description", notification.getDescription());
            payload.put("createdAt", notification.getCreatedAt());
            payload.put("isRead", notification.isRead());
            this.sendCustomWebSocketNotificationToUser(username, payload);
        }
    }

}
