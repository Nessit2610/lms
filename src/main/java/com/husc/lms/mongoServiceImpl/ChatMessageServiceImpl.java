package com.husc.lms.mongoServiceImpl;

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

import java.time.OffsetDateTime;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

        private final ChatBoxRepository chatBoxRepo;
        private final ChatBoxMemberRepository memberRepo;
        private final ChatMessageRepository messageRepo;
        private final AccountRepository accountRepo;
        private final ChatMessageStatusRepository chatMessageStatusRepository;

        private final Function<String, String> fileExtension = filename -> Optional.ofNullable(filename)
                        .filter(name -> name.contains("."))
                        .map(name -> "." + name.substring(name.lastIndexOf('.') + 1))
                        .orElse("");

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

        private String uploadFile(String id, MultipartFile file, String type) {
                if (file == null || file.isEmpty()) {
                        throw new RuntimeException("File is empty");
                }

                String extension = fileExtension.apply(file.getOriginalFilename());
                validateFileExtension(type, extension);

                String folder = getFolderFromType(type);
                String directory = Constant.CHAT_DIRECTORY + folder + "/";
                File dir = new File(directory);
                if (!dir.exists()) {
                        dir.mkdirs();
                }

                String fileName = id + extension;
                Path filePath = Paths.get(directory + fileName);
                try {
                        Files.copy(file.getInputStream(), filePath);
                        return directory + fileName;
                } catch (IOException e) {
                        throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
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

                ChatMessage.ChatMessageBuilder messageBuilder = ChatMessage.builder()
                                .chatBoxId(chatBoxId)
                                .senderAccount(senderAccount)
                                .content(content)
                                .createdAt(OffsetDateTime.now());

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
                chatBox.setLastMessageAt(message.getCreatedAt());
                chatBox.setLastMessageBy(senderAccount);
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
                                        .readAt(isSender ? OffsetDateTime.now() : null)
                                        .build();
                        statuses.add(status);
                }

                if (!statuses.isEmpty()) {
                        chatMessageStatusRepository.saveAll(statuses);
                        System.out.println(
                                        "[DEBUG] Saved " + statuses.size() + " message statuses for messageId: "
                                                        + message.getId());
                }

                return message;
        }

        @Override
        public Page<ChatMessage> getMessagesByChatBoxId(String chatBoxId, Pageable pageable) {
                return messageRepo.findByChatBoxId(chatBoxId, pageable);
        }
}
