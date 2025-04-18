package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.CommentsOfChapterInLessonOfCourseResponse;
import com.husc.lms.dto.response.FlatCommentInfo;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;

import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CommentRepository;
//import com.husc.lms.repository.CourseRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final ChapterRepository chapterRepository;
//	private final CourseRepository courseRepository;
	
    public Comment saveComment(Comment comment) {
        comment.setCreatedDate(OffsetDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByChapter(Chapter chapter) {
        return commentRepository.findByChapterOrderByCreatedDateDesc(chapter);
    }
    
    public List<CommentChapterResponse> getCommentsByChapterId(String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        List<Comment> comments = getCommentsByChapter(chapter); // hoặc gọi trực tiếp repository nếu bạn muốn

        return comments.stream()
                .map(comment -> new CommentChapterResponse(
                        comment.getAccount().getUsername(),
                        comment.getDetail(),
                        comment.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }
    
    public List<CommentChapterResponse> getUnreadCommentsByCourseId(String courseId) {
    	return commentRepository.findUnreadCommentsByCourseId(courseId);
    }

    public CommentsOfChapterInLessonOfCourseResponse getStructuredUnreadComments(String courseId) {
        List<FlatCommentInfo> flatList = commentRepository.findStructuredUnreadComments(courseId);

        if (flatList.isEmpty()) return null;

        CommentsOfChapterInLessonOfCourseResponse response = new CommentsOfChapterInLessonOfCourseResponse();
        response.setCourseId(flatList.get(0).getCourseId());
        response.setCourseTitle(flatList.get(0).getCourseTitle());

        Map<String, CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> lessonMap = new LinkedHashMap<>();

        int totalCommentsOfCourse = 0;

        for (FlatCommentInfo flat : flatList) {
            totalCommentsOfCourse++;

            lessonMap.putIfAbsent(flat.getLessonId(),
                new CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters(
                    flat.getLessonId(),
                    flat.getLessonId(),
                    flat.getLessonOrder(),
                    new ArrayList<>()
                )
            );

            var lesson = lessonMap.get(flat.getLessonId());

            var chapterList = lesson.getChapters();
            var chapter = chapterList.stream()
                .filter(ch -> ch.getChapterId().equals(flat.getChapterId()))
                .findFirst()
                .orElseGet(() -> {
                    var ch = new CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments(
                        flat.getChapterId(),
                        flat.getChapterTitle(),
                        flat.getChapterOrder(),
                        0, // totalComments tạm thời là 0
                        new ArrayList<>()
                    );
                    chapterList.add(ch);
                    return ch;
                });

            chapter.getComments().add(new CommentsOfChapterInLessonOfCourseResponse.CommentResponse(
                flat.getUsername(),
                flat.getDetail(),
                flat.getCreatedDate()
            ));

            chapter.setTotalCommentsOfChapter(chapter.getTotalCommentsOfChapter() + 1);
        }

        List<CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> sortedLessons = new ArrayList<>(lessonMap.values());
        sortedLessons.sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters::getLessonOrder));
        for (var lesson : sortedLessons) {
            lesson.getChapters().sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments::getChapterOrder));
        }

        response.setLessons(sortedLessons);
        response.setTotalCommentsOfCourse(totalCommentsOfCourse);

        return response;
    }

}
