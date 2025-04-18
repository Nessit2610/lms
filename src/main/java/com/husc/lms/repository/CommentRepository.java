package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.FlatCommentInfo;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>{
    List<Comment> findByChapterOrderByCreatedDateDesc(Chapter chapter);
    
    @Query("SELECT new com.husc.lms.dto.response.CommentChapterResponse(c.account.username, c.detail, c.createdDate) FROM Comment c "
    	       + "WHERE c.course.id = :courseId "
    	       + "AND c.deletedDate IS NULL")
    List<CommentChapterResponse> findUnreadCommentsByCourseId(@Param("courseId") String courseId);

    
    @Query("""
    	    SELECT new com.husc.lms.dto.response.FlatCommentInfo(
    	        c.course.id,
    	        c.course.name,
    	        ch.lesson.id,
    	        ch.lesson.order,
    	        ch.id,
    	        ch.order,
    	        ch.name,
    	        c.account.username,
    	        c.detail,
    	        c.createdDate
    	    )
    	    FROM Comment c
    	    JOIN c.chapter ch
    	    JOIN ch.lesson l
    	    WHERE c.course.id = :courseId
    	      AND c.deletedDate IS NULL
    	    ORDER BY l.order, ch.order, c.createdDate
    	""")
    	List<FlatCommentInfo> findStructuredUnreadComments(@Param("courseId") String courseId);


}
