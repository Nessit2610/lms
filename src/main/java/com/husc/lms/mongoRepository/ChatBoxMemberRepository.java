package com.husc.lms.mongoRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.entity.Account;

@Repository
public interface ChatBoxMemberRepository extends MongoRepository<ChatBoxMember, String> {
    List<ChatBoxMember> findByAccountUsername(String accountUsername);
    
    List<ChatBoxMember> findByChatBoxId(String chatBoxId);

    Page<ChatBoxMember> findByChatBoxId(String chatBoxId, Pageable pageable);

    boolean existsByChatBoxIdAndAccountUsername(String chatBoxId, String accountUsername);

    void deleteByChatBoxIdAndAccountUsername(String chatBoxId, String accountUsername);
}
