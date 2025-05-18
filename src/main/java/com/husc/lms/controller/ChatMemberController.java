//package com.husc.lms.controller;
//
//import java.util.List;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.husc.lms.dto.response.APIResponse;
//import com.husc.lms.dto.response.ChatMemberSearchResponse;
//import com.husc.lms.mongoService.ChatBoxMemberService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/chatmember")
//@RequiredArgsConstructor
//public class ChatMemberController {
//
//    private final ChatBoxMemberService chatBoxMemberService;
//
//    @GetMapping("/search")
//    public APIResponse<List<ChatMemberSearchResponse>> searchPotentialMembers(
//            @RequestParam String chatBoxId,
//            @RequestParam String searchString) {
//
//        List<ChatMemberSearchResponse> searchMembers = chatBoxMemberService.findAccountsNotInChatBox(chatBoxId,
//                searchString);
//        return APIResponse.<List<ChatMemberSearchResponse>>builder()
//        		.result(searchMembers)
//        		.build();
//    }
//
//}
