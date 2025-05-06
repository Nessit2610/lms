package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReadStatus;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.enums.CommentType;

import jakarta.transaction.Transactional;

@Repository
public interface CommentReadStatusRepository extends JpaRepository<CommentReadStatus, String>{
	
	@Modifying
	@Transactional
	@Query("UPDATE CommentReadStatus crs SET crs.isRead = true WHERE crs.comment IN :comments AND crs.account = :account")
	void setCommentsAsReadByAccount(List<Comment> comments, Account account);
	
	@Modifying
	@Query("UPDATE CommentReadStatus crs SET crs.isRead = true " +
	       "WHERE crs.account = :account AND crs.comment IN :comments AND crs.commentType = :type")
	void markCommentsAsReadByAccount(@Param("comments") List<Comment> comments,
	                                 @Param("account") Account account,
	                                 @Param("type") CommentType type);

	@Modifying
	@Query("UPDATE CommentReadStatus crs SET crs.isRead = true " +
	       "WHERE crs.account = :account AND crs.commentReply IN :replies AND crs.commentType = :type")
	void markRepliesAsReadByAccount(@Param("replies") List<CommentReply> replies,
	                                @Param("account") Account account,
	                                @Param("type") CommentType type);
		
	@Query("""
		    SELECT COUNT(crs) FROM CommentReadStatus crs
		    WHERE crs.comment.course = :course
		      AND crs.comment.deletedDate IS NULL
		      AND crs.comment.course.deletedDate IS NULL
		""")
	int countAllValidCommentReadStatusesByCourse(@Param("course") Course course);

	@Query("""
	    SELECT COUNT(crs) FROM CommentReadStatus crs
	    WHERE crs.commentReply.course = :course
	      AND crs.commentReply.deletedDate IS NULL
	      AND crs.commentReply.course.deletedDate IS NULL
		""")
	int countAllValidCommentReplyReadStatusesByCourse(@Param("course") Course course);
	@Query("""
		    SELECT COUNT(crs) FROM CommentReadStatus crs
		    WHERE crs.comment.course = :course
		      AND crs.comment.deletedDate IS NULL
		      AND crs.comment.course.deletedDate IS NULL
		      AND crs.isRead = false
		""")
	int countUnreadValidCommentReadStatusesByCourse(@Param("course") Course course);

	@Query("""
	    SELECT COUNT(crs) FROM CommentReadStatus crs
	    WHERE crs.commentReply.course = :course
	      AND crs.commentReply.deletedDate IS NULL
	      AND crs.commentReply.course.deletedDate IS NULL
	      AND crs.isRead = false
		""")
	int countUnreadValidCommentReplyReadStatusesByCourse(@Param("course") Course course);
	@Query("""
		    SELECT COUNT(crs) FROM CommentReadStatus crs
		    WHERE crs.comment.chapter.lesson = :lesson
		      AND crs.comment.deletedDate IS NULL
		      AND crs.comment.chapter.deletedDate IS NULL
		      AND crs.comment.chapter.lesson.deletedDate IS NULL
		""")
	int countAllValidCommentReadStatusesByLessonInCourse(@Param("lesson") Lesson lesson);

	@Query("""
	    SELECT COUNT(crs) FROM CommentReadStatus crs
	    WHERE crs.commentReply.chapter.lesson = :lesson
	      AND crs.commentReply.deletedDate IS NULL
	      AND crs.commentReply.chapter.deletedDate IS NULL
	      AND crs.commentReply.chapter.lesson.deletedDate IS NULL
		""")
	int countAllValidCommentReplyReadStatusesByLessonInCourse(@Param("lesson") Lesson lesson);
	@Query("""
		    SELECT COUNT(crs) FROM CommentReadStatus crs
		    WHERE crs.comment.chapter.lesson = :lesson
		      AND crs.isRead = false
		      AND crs.comment.deletedDate IS NULL
		      AND crs.comment.chapter.deletedDate IS NULL
		      AND crs.comment.chapter.lesson.deletedDate IS NULL
		""")
	int countUnreadCommentReadStatusesByLessonInCourse(@Param("lesson") Lesson lesson);

	@Query("""
	    SELECT COUNT(crs) FROM CommentReadStatus crs
	    WHERE crs.commentReply.chapter.lesson = :lesson
	      AND crs.isRead = false
	      AND crs.commentReply.deletedDate IS NULL
	      AND crs.commentReply.chapter.deletedDate IS NULL
	      AND crs.commentReply.chapter.lesson.deletedDate IS NULL
		""")
	int countUnreadCommentReplyReadStatusesByLessonInCourse(@Param("lesson") Lesson lesson);
	
	@Query("""
		    SELECT COUNT(crs) FROM CommentReadStatus crs
		    WHERE crs.comment.chapter = :chapter
		      AND crs.comment.deletedDate IS NULL
		      AND crs.comment.chapter.deletedDate IS NULL
		""")
	int countAllValidCommentReadStatusesByChapter(@Param("chapter") Chapter chapter);

	@Query("""
	    SELECT COUNT(crs) FROM CommentReadStatus crs
	    WHERE crs.commentReply.chapter = :chapter
	      AND crs.commentReply.deletedDate IS NULL
	      AND crs.commentReply.chapter.deletedDate IS NULL
		""")
	int countAllValidCommentReplyReadStatusesByChapter(@Param("chapter") Chapter chapter);
	
	@Query("""
		    SELECT COUNT(crs) FROM CommentReadStatus crs
		    WHERE crs.comment.chapter = :chapter
		      AND crs.comment.deletedDate IS NULL
		      AND crs.comment.chapter.deletedDate IS NULL
		      AND crs.isRead = false
		""")
	int countUnreadCommentReadStatusesByChapter(@Param("chapter") Chapter chapter);

	@Query("""
	    SELECT COUNT(crs) FROM CommentReadStatus crs
	    WHERE crs.commentReply.chapter = :chapter
	      AND crs.commentReply.deletedDate IS NULL
	      AND crs.commentReply.chapter.deletedDate IS NULL
	      AND crs.isRead = false
		""")
	int countUnreadCommentReplyReadStatusesByChapter(@Param("chapter") Chapter chapter);

}
