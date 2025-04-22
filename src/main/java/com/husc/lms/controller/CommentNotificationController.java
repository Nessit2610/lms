//package com.husc.lms.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.husc.lms.dto.response.CommentNotificationResponse;
//import com.husc.lms.service.CommentNotificationService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/notifications")
//@RequiredArgsConstructor
//public class CommentNotificationController {
//	private final CommentNotificationService commentNotificationService;
//
//    @GetMapping("/comments/unread")
//    public CommentNotificationResponse getUnreadCommentNotifications() {
//        return commentNotificationService.getAllUnreadCommentNotificationOfAccount();
//    }
//}