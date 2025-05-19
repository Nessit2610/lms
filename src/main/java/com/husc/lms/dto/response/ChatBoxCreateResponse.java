package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.mongoEntity.ChatBoxMember;

import jakarta.persistence.Id;
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
                private String accountUsername;
                private String accountFullname;
                private String avatar;
        }
}
