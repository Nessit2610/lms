package com.husc.lms.mongoRepository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.mongoEntity.ChatMessageStatus;

@Repository
public interface ChatMessageStatusRepository extends MongoRepository<ChatMessageStatus, String>{

}
