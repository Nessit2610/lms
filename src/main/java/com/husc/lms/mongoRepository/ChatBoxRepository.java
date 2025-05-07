package com.husc.lms.mongoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.mongoEntity.ChatBox;

@Repository
public interface ChatBoxRepository extends MongoRepository<ChatBox, String> {
    Optional<ChatBox> findById(String id);

    List<ChatBox> findByIsGroupFalse();

    Page<ChatBox> findByIdInAndIsGroupFalse(List<String> chatBoxIds, Pageable pageable);

}
