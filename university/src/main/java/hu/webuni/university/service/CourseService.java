package hu.webuni.university.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import hu.webuni.university.model.Course;
import hu.webuni.university.model.HistoryData;
import hu.webuni.university.model.QCourse;
import hu.webuni.university.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	
	private final CourseRepository courseRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	//1. verzió: lapozás nélkül
//	@Transactional
//	public List<Course> searchCourses(Predicate predicate) {
//		List<Course> courses = courseRepository.findAll(predicate, "Course.students");
//		courses = courseRepository.findAll(QCourse.course.in(courses), "Course.teachers");
//		return courses;
//	}
	
	@Transactional
	@Cacheable("courseSearchResults")
	public List<Course> searchCourses(Predicate predicate, Pageable pageable) {
		
		Page<Course> coursePage = courseRepository.findAll(predicate, pageable);
		
		BooleanExpression courseIdInPredicate = QCourse.course.in(coursePage.getContent());
		
		List<Course> courses = courseRepository.findAll(courseIdInPredicate, "Course.students", Sort.unsorted());
		courses = courseRepository.findAll(courseIdInPredicate, "Course.teachers", pageable.getSort());
		return courses;
	}
	@Transactional
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<HistoryData<Course>> getHistoryById(int id) {

		List resultList = AuditReaderFactory.get(em)
			.createQuery()
			.forRevisionsOfEntity(Course.class, false, true)
			.add(AuditEntity.property("id").eq(id))
			.getResultList().stream().map(o -> {
					Object[] objArray = (Object[]) o;
					
					DefaultRevisionEntity defaultRevisionEntity = (DefaultRevisionEntity) objArray[1];
					RevisionType revType = (RevisionType) objArray[2];
					
					Course course = (Course) objArray[0];
					course.getStudents().size();
					course.getTeachers().size();
					
					HistoryData<Course> historyData = 
						new HistoryData<>(
							course, revType,
							defaultRevisionEntity.getId(), defaultRevisionEntity.getRevisionDate());
					return historyData;
				}).toList();
		return resultList;
	}
	
	@Transactional
	public Course getVersionAt(Integer id, OffsetDateTime at) {
		long epochMilli = at.toInstant().toEpochMilli();
		
		List resultList = AuditReaderFactory.get(em)
				.createQuery()
				.forRevisionsOfEntity(Course.class, true, false)
				.add(AuditEntity.property("id").eq(id))
				.add(AuditEntity.revisionProperty("timestamp").le(epochMilli))
				.addOrder(AuditEntity.revisionProperty("timestamp").desc())
				.setMaxResults(1)
				.getResultList();
		
		if(!resultList.isEmpty()) {
			Course course = (Course) resultList.get(0);
			course.getStudents().size();
			course.getTeachers().size();
			return course;
		}
		
		return null;
	}


}
