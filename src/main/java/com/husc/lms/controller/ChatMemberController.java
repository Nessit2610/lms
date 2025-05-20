package com.husc.lms.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.ChatMemberSearchResponse;
import com.husc.lms.mongoService.ChatBoxMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chatmember")
@RequiredArgsConstructor
public class ChatMemberController {

    private final ChatBoxMemberService chatBoxMemberService;

    @GetMapping("/search")
    public APIResponse<Page<ChatMemberSearchResponse>> searchPotentialMembers(
            @RequestParam String chatBoxId,
            @RequestParam String searchString,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<ChatMemberSearchResponse> searchMembers = chatBoxMemberService.findAccountsNotInChatBox(chatBoxId,
                searchString, pageNumber, pageSize);
        return APIResponse.<Page<ChatMemberSearchResponse>>builder()
                .result(searchMembers)
                .build();
    }

}
