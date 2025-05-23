package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByChapterOrderByCreatedDateDesc(Chapter chapter);

    @Query("SELECT new com.husc.lms.dto.response.CommentChapterResponse(c.id ,c.account.username, c.detail, c.createdDate) FROM Comment c "
            + "WHERE c.course.id = :courseId "
            + "AND c.deletedDate IS NULL")
    List<CommentChapterResponse> findUnreadCommentsByCourseId(@Param("courseId") String courseId);

    @Query("SELECT new com.husc.lms.dto.response.CommentChapterResponse(c.id, c.account.username, c.detail, c.createdDate) "
            + "FROM Comment c WHERE c.course.id = :courseId "
            + "AND c.deletedDate IS NULL")
    Page<CommentChapterResponse> findUnreadCommentsByCourseId(@Param("courseId") String courseId, Pageable pageable);

    Page<Comment> findByChapterOrderByCreatedDateDesc(Chapter chapter, Pageable pageable);

    Page<Comment> findByChapterAndDeletedDateIsNullOrderByCreatedDateDesc(Chapter chapter, Pageable pageable);

    @Query("""
                SELECT c
                FROM Comment c
                WHERE c.course IN (
                    SELECT l.course
                    FROM Lesson l
                    WHERE l IN :lessons AND l.deletedDate IS NULL AND l.course.deletedDate IS NULL
                )
                AND c.course.deletedDate IS NULL
            """)
    List<Comment> getCommentsByLessons(@Param("lessons") List<Lesson> lessons);

    @Query("SELECT c FROM Comment c WHERE c.course = :course AND c.deletedDate IS NULL")
    List<Comment> findByCourseHasDeleteDateNull(@Param("course") Course course);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.deletedDate IS NULL")
    Page<Comment> findByPostId(String postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.chapter.id = :chapterId AND c.account.username = :username AND c.deletedDate IS NULL ORDER BY c.createdDate DESC")
    Page<Comment> findByChapterIdAndUsernameOrderByCreatedDateDesc(@Param("chapterId") String chapterId,
            @Param("username") String username, Pageable pageable);
}
