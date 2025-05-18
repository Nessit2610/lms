package com.husc.lms.mongoServiceImpl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoEntity.ChatMessageStatus;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoRepository.ChatMessageStatusRepository;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

        private final ChatMessageRepository messageRepo;
        private final ChatBoxRepository chatBoxRepo;
        private final ChatBoxMemberRepository memberRepo;
        private final ChatMessageStatusRepository statusRepo;
        private final AccountRepository accountRepo;

        private final Function<String, String> fileExtension = filename -> Optional.ofNullable(filename)
                        .filter(name -> name.contains("."))
                        .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
                        .orElse("");

        private OffsetDateTime convertToOffsetDateTime(Date date) {
                if (date == null) {
                        return null;
                }
                return date.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
        }

        private void validateFileExtension(String type, String extension) {
                Set<String> imageExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif");
                Set<String> videoExtensions = Set.of(".mp4", ".avi", ".mov");
                Set<String> fileExtensions = Set.of(".pdf", ".doc", ".docx", ".txt");

                switch (type.toLowerCase()) {
                        case "image" -> {
                                if (!imageExtensions.contains(extension)) {
                                        throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
                                }
                        }
                        case "video" -> {
                                if (!videoExtensions.contains(extension)) {
                                        throw new AppException(ErrorCode.INVALID_VIDEO_TYPE);
                                }
                        }
                        case "file" -> {
                                if (!fileExtensions.contains(extension)) {
                                        throw new AppException(ErrorCode.INVALID_FILE_TYPE);
                                }
                        }
                        default -> throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);
                }
        }

        private String getFolderFromType(String type) {
                return switch (type.toLowerCase()) {
                        case "photo", "image" -> "images";
                        case "video" -> "videos";
                        case "file", "document" -> "files";
                        default -> throw new RuntimeException("Unsupported file type: " + type);
                };
        }

        private String uploadFile(String path, MultipartFile file, String fileType) {
                try {
                        String uploadDir = Constant.CHAT_DIRECTORY + "/" + path;
                        Path uploadPath = Paths.get(uploadDir);
                        if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                        }

                        String fileName = file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        return path + "/" + fileName;
                } catch (IOException e) {
                        throw new AppException(ErrorCode.FILE_UPLOAD_FAILED,
                                        "Failed to upload file: " + e.getMessage());
                }
        }

        @Override
        public ChatMessage sendMessage(String chatBoxId, String senderAccount, String content, MultipartFile file,
                        String fileType) {
                Account acc = accountRepo.findByUsernameAndDeletedDateIsNull(senderAccount)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Sender account not found: " + senderAccount));

                ChatBox chatBox = chatBoxRepo.findById(chatBoxId)
                                .orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND,
                                                "ChatBox not found with id: " + chatBoxId));

                Date now = new Date();
                ChatMessage.ChatMessageBuilder messageBuilder = ChatMessage.builder()
                                .chatBoxId(chatBoxId)
                                .senderAccount(senderAccount)
                                .content(content)
                                .createdAt(now);

                String filePath = null;
                String originalFilename = null;

                if (file != null && !file.isEmpty()) {
                        originalFilename = file.getOriginalFilename();
                        // Upload file and get URL
                        filePath = uploadFile(chatBoxId + "/" + UUID.randomUUID().toString(), file, fileType);

                        messageBuilder.path(filePath);
                        messageBuilder.type(fileType);
                        messageBuilder.filename(originalFilename);
                }

                ChatMessage message = messageBuilder.build();
                message = messageRepo.save(message);

                // Update last message in chatbox
                if (file != null && !file.isEmpty() && (content == null || content.trim().isEmpty())) {
                        chatBox.setLastMessage(
                                        "[" + (fileType != null ? fileType : "File") + ": " + originalFilename + "]");
                } else {
                        chatBox.setLastMessage(content);
                }
                chatBox.setLastMessageAt(now);
                chatBox.setLastMessageBy(senderAccount);
                chatBox.setUpdatedAt(now);
                chatBoxRepo.save(chatBox);

                List<ChatBoxMember> members = memberRepo.findByChatBoxId(chatBoxId);
                if (members.isEmpty()) {
                        System.err.println(
                                        "Critical: No members found for ChatBox id: " + chatBoxId
                                                        + ". Cannot create message statuses.");
                        throw new AppException(ErrorCode.CHATBOX_MEMBER_NOT_FOUND,
                                        "No members in chatbox " + chatBoxId + ", cannot create statuses.");
                }

                List<ChatMessageStatus> statuses = new ArrayList<>();
                for (ChatBoxMember member : members) {
                        boolean isSender = member.getAccountUsername().equals(senderAccount);
                        ChatMessageStatus status = ChatMessageStatus.builder()
                                        .messageId(message.getId())
                                        .chatBoxId(chatBoxId)
                                        .accountUsername(member.getAccountUsername())
                                        .isRead(isSender)
                                        .readAt(isSender ? now : null)
                                        .build();
                        statuses.add(status);
                }
                statusRepo.saveAll(statuses);

                return message;
        }

        @Override
        public Page<ChatMessage> getMessagesByChatBoxId(String chatBoxId, Pageable pageable) {
                return messageRepo.findByChatBoxId(chatBoxId, pageable);
        }
}
