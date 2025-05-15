package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.husc.lms.entity.Account;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.repository.AccountRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBoxCreateResponse {
    private String chatBoxId;

    private boolean isGroup;

    private String createdBy;

    private String nameOfCreatedBy;

    private OffsetDateTime createdAt;

    private String nameOfChatBox;

    private List<ListOfMember> listMemmber;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListOfMember {
        private String memberAccountUsername;
        private String memberFullname;
        private String memberAvatar;
    }

    public static ChatBoxCreateResponse fromChatBox(ChatBox chatBox, String currentMemberUsernameForContext,
            AccountRepository accountRepository) {
        if (chatBox == null)
            return null;

        Account creatorAccount = accountRepository.findByUsernameAndDeletedDateIsNull(chatBox.getCreatedBy())
                .orElse(null);
        String creatorFullName = chatBox.getCreatedBy();
        if (creatorAccount != null) {
            creatorFullName = creatorAccount.getStudent() != null ? creatorAccount.getStudent().getFullName()
                    : (creatorAccount.getTeacher() != null ? creatorAccount.getTeacher().getFullName()
                            : chatBox.getCreatedBy());
        }

        List<ListOfMember> memberDetails = chatBox.getMemberAccountUsernames().stream()
                .map(username -> {
                    Account acc = accountRepository.findByUsernameAndDeletedDateIsNull(username).orElse(null);
                    String fullname = username;
                    String avatar = "";
                    if (acc != null) {
                        fullname = acc.getStudent() != null ? acc.getStudent().getFullName()
                                : (acc.getTeacher() != null ? acc.getTeacher().getFullName() : username);
                        avatar = acc.getStudent() != null ? acc.getStudent().getAvatar()
                                : (acc.getTeacher() != null ? acc.getTeacher().getAvatar() : "");
                    }
                    return ListOfMember.builder()
                            .memberAccountUsername(username)
                            .memberFullname(fullname)
                            .memberAvatar(avatar)
                            .build();
                })
                .collect(Collectors.toList());

        return ChatBoxCreateResponse.builder()
                .chatBoxId(chatBox.getId())
                .isGroup(chatBox.isGroup())
                .createdBy(chatBox.getCreatedBy())
                .nameOfCreatedBy(creatorFullName)
                .createdAt(chatBox.getCreatedAt() != null
                        ? chatBox.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime()
                        : null)
                .nameOfChatBox(chatBox.getName())
                .listMemmber(memberDetails)
                .build();
    }
}
