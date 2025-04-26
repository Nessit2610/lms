package com.husc.lms.mongoServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
	
	private final ChatBoxRepository chatBoxRepo;
    private final ChatBoxMemberRepository memberRepo;
    private final ChatMessageRepository messageRepo;
    private final AccountRepository accountRepo;

	
	@Override
	public ChatBox getOrCreatePrivateChatBox(String senderId, String receiverId) {
		// Tìm tất cả các ChatBox private (isGroup == false)
        List<ChatBox> privateChats = chatBoxRepo.findByIsGroupFalse();
        for (ChatBox box : privateChats) {
            List<ChatBoxMember> members = memberRepo.findByChatBoxId(box.getId());
            Set<String> memberIds = members.stream().map(ChatBoxMember::getAccountId).collect(Collectors.toSet());
            if (memberIds.containsAll(Set.of(senderId, receiverId)) && memberIds.size() == 2) {
                return box;
            }
        }
        // Nếu chưa có, tạo mới ChatBox và thêm 2 thành viên.
        ChatBox newBox = ChatBox.builder()
                .isGroup(false)
                .createdAt(new Date())
                .build();
        ChatBox savedBox = chatBoxRepo.save(newBox);
        memberRepo.save(ChatBoxMember.builder()
                .chatBoxId(savedBox.getId())
                .accountId(senderId)
                .joinedAt(new Date())
                .build());
        memberRepo.save(ChatBoxMember.builder()
                .chatBoxId(savedBox.getId())
                .accountId(receiverId)
                .joinedAt(new Date())
                .build());
        return savedBox;
    }

	@Override
    public ChatMessage saveMessage(String chatBoxId, String senderId, String content) {
        ChatMessage msg = ChatMessage.builder()
                .chatBoxId(chatBoxId)
                .senderId(senderId)
                .content(content)
                .createdAt(new Date())
                .read(false)
                .build();
        return messageRepo.save(msg);
    }

	@Override
	public Page<ChatBox> getOneToOneChatBoxesForAccount(Pageable pageable) {
		var context = SecurityContextHolder.getContext();
		String username = context.getAuthentication().getName();

		
		Account currentAccount = accountRepo.findByUsernameAndDeletedDateIsNull(username).orElseThrow();
		
		List<ChatBoxMember> memberships = memberRepo.findByAccountId(currentAccount.getId());
        List<String> chatBoxIds = memberships.stream()
                                             .map(ChatBoxMember::getChatBoxId)
                                             .distinct()
                                             .toList();
        	
		return chatBoxRepo.findByIdInAndIsGroupFalse(chatBoxIds, pageable);
	}

	
	
}
