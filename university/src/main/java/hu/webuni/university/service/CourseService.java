package hu.webuni.university.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import hu.webuni.university.model.Course;
import hu.webuni.university.model.QCourse;
import hu.webuni.university.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	
	private final CourseRepository courseRepository;
	
	//1. verzió: lapozás nélkül
//	@Transactional
//	public List<Course> searchCourses(Predicate predicate) {
//		List<Course> courses = courseRepository.findAll(predicate, "Course.students");
//		courses = courseRepository.findAll(QCourse.course.in(courses), "Course.teachers");
//		return courses;
//	}
	
	@Transactional
	public List<Course> searchCourses(Predicate predicate, Pageable pageable) {
		
		Page<Course> coursePage = courseRepository.findAll(predicate, pageable);
		
		BooleanExpression courseIdInPredicate = QCourse.course.in(coursePage.getContent());
		
		List<Course> courses = courseRepository.findAll(courseIdInPredicate, "Course.students", Sort.unsorted());
		courses = courseRepository.findAll(courseIdInPredicate, "Course.teachers", pageable.getSort());
		return courses;
	}
}
