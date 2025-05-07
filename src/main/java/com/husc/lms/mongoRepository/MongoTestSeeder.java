package com.husc.lms.mongoRepository;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoEntity.ChatMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MongoTestSeeder implements CommandLineRunner {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatBoxRepository chatBoxRepository;
    private final ChatBoxMemberRepository chatBoxMemberRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu không có ChatBox nào trong MongoDB
        if (chatBoxRepository.count() == 0) {
            // Tạo ChatBox (phòng chat)
            ChatBox chatBox = ChatBox.builder()
                    .isGroup(true) // là group chat
                    .createdAt(new Date())
                    .createdBy("acc1") // người tạo phòng
                    .name("LMS Group Chat") // tên phòng
                    .build();
            chatBoxRepository.save(chatBox);

            // Tạo ChatBoxMember (thành viên tham gia chatbox)
            ChatBoxMember member1 = ChatBoxMember.builder()
                    .chatBoxId(chatBox.getId()) // liên kết với chatBox vừa tạo
                    .accountId("acc1") // tài khoản tham gia
                    .joinedAt(new Date()) // thời gian tham gia
                    .build();

            ChatBoxMember member2 = ChatBoxMember.builder()
                    .chatBoxId(chatBox.getId()) // liên kết với chatBox vừa tạo
                    .accountId("acc2") // tài khoản tham gia
                    .joinedAt(new Date()) // thời gian tham gia
                    .build();

            chatBoxMemberRepository.save(member1);
            chatBoxMemberRepository.save(member2);

            // Tạo ChatMessage (tin nhắn trong phòng chat)
            ChatMessage message1 = ChatMessage.builder()
                    .chatBoxId(chatBox.getId()) // liên kết với chatBox
                    .senderId("acc1") // người gửi
                    .content("Hello everyone!") // nội dung tin nhắn
                    .createdAt(new Date()) // thời gian gửi
                    .build();

            ChatMessage message2 = ChatMessage.builder()
                    .chatBoxId(chatBox.getId()) // liên kết với chatBox
                    .senderId("acc2") // người gửi
                    .content("Hi, how are you?") // nội dung tin nhắn
                    .createdAt(new Date()) // thời gian gửi
                    .build();

            chatMessageRepository.save(message1);
            chatMessageRepository.save(message2);

            System.out.println("✅ Dữ liệu mẫu đã được lưu vào MongoDB.");
        } else {
            System.out.println("🔴 Dữ liệu đã có trong MongoDB, không tạo mới.");
        }
    }
}
