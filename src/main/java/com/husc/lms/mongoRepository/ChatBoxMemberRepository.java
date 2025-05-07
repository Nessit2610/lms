package com.husc.lms.mongoRepository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.mongoEntity.ChatBoxMember;

@Repository
public interface ChatBoxMemberRepository extends MongoRepository<ChatBoxMember, String> {
    List<ChatBoxMember> findByAccountId(String accountId);

    List<ChatBoxMember> findByChatBoxId(String chatBoxId);

    boolean existsByChatBoxIdAndAccountId(String chatBoxId, String accountId);
}
