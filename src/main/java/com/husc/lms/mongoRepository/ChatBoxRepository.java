package com.husc.lms.mongoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.husc.lms.mongoEntity.ChatBox;

@Repository
public interface ChatBoxRepository extends MongoRepository<ChatBox, String> {
    Optional<ChatBox> findById(String id);

    List<ChatBox> findByIsGroupFalse();

    Page<ChatBox> findByIdInAndIsGroupFalse(List<String> chatBoxIds, Pageable pageable);

    /**
     * Tìm tất cả chatbox theo danh sách ID
     * 
     * @param chatBoxIds Danh sách ID của chatbox
     * @param pageable   Thông tin phân trang
     * @return Page chứa danh sách chatbox
     */
    Page<ChatBox> findByIdIn(List<String> chatBoxIds, Pageable pageable);

    /**
     * Cập nhật tên của chatbox
     * 
     * @param id   ID của chatbox
     * @param name Tên mới của chatbox
     */
    @Query("{ '_id' : ?0 }, { '$set' : { 'name' : ?1 } }")
    void updateNameById(String id, String name);
}
