package com.husc.lms.mongoRepository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.mongoEntity.ChatMessageStatus;
import java.util.List;

@Repository
public interface ChatMessageStatusRepository extends MongoRepository<ChatMessageStatus, String> {

    List<ChatMessageStatus> findByChatBoxIdAndIsReadFalse(String chatBoxId);
}
