package com.husc.lms.mapperImpl;

import org.springframework.stereotype.Component;

import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.dto.response.LessonMaterialResponse;
import com.husc.lms.dto.response.LessonQuizResponse;
import com.husc.lms.dto.response.LessonResponse;
import com.husc.lms.entity.Course;
import com.husc.lms.mapper.ChapterMapper;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.LessonMapper;
import com.husc.lms.mapper.LessonMaterialMapper;
import com.husc.lms.mapper.LessonQuizMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CourseMapperImplCustom {
	
	private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;
    private final LessonMaterialMapper lessonMaterialMapper;
    private final LessonQuizMapper lessonQuizMapper;
    private final ChapterMapper chapterMapper;

    public CourseResponse toFilteredCourseResponse(Course course) {
        CourseResponse response = courseMapper.toCourseResponse(course);

        response.setLesson(
            course.getLesson().stream()
                .filter(lesson -> lesson.getDeletedDate() == null)
                .map(lesson -> {
                    LessonResponse lessonRes = lessonMapper.toLessonResponse(lesson);

                    lessonRes.setLessonMaterial(
                        lesson.getLessonMaterial().stream()
                            .filter(m -> m.getDeletedDate() == null)
                            .map(lessonMaterialMapper::toLessonMaterialResponse)
                            .toList()
                    );

                    lessonRes.setLessonQuiz(
                        lesson.getLessonQuiz().stream()
                            .filter(q -> q.getDeletedDate() == null)
                            .map(lessonQuizMapper::toLessonQuizResponse)
                            .toList()
                    );

                    lessonRes.setChapter(
                        lesson.getChapter().stream()
                            .filter(ch -> ch.getDeletedDate() == null)
                            .map(chapterMapper::toChapterResponse)
                            .toList()
                    );

                    return lessonRes;
                })
                .toList()
        );

        return response;
    }
}
