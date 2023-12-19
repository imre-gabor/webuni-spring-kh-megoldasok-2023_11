package hu.webuni.university.repository;


import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import hu.webuni.university.model.Course;
import hu.webuni.university.model.QCourse;

public interface CourseRepository extends 
	JpaRepository<Course, Integer>,
	QuerydslPredicateExecutor<Course>,
	QuerydslBinderCustomizer<QCourse>,
	QuerydslWithEntityGraphRepository<Course, Integer>{
	
	
	//1. megoldási ötlet: nem lehet dinamkusan dönteni, hogy kellenek-e a kapcsolatok + Descartes-szorzat
//	@Override
//	@EntityGraph(attributePaths = {"teachers", "students"})
//	Iterable<Course> findAll(Predicate predicate);
	
	//2. megoldási ötlet: nem tud query impl. generálódni
//	@EntityGraph(attributePaths = {"teachers"})	
//	Iterable<Course> findAllWithTeachers(Predicate predicate);
//	
//	@EntityGraph(attributePaths = {"students"})
//	Iterable<Course> findAllWithStudents(Predicate predicate);

	@Override
	default void customize(QuerydslBindings bindings, QCourse course) {

		bindings.bind(course.name).first((path, value) -> path.startsWithIgnoreCase(value));
		bindings.bind(course.teachers.any().name).first((path, value) -> path.startsWithIgnoreCase(value));
		
		bindings.bind(course.students.any().semester).all((path, values) -> {
			if(values.size() !=2)
				return Optional.empty();
			
			Iterator<? extends Integer> iterator = values.iterator();
			Integer from = iterator.next();
			Integer to = iterator.next();
			
			return Optional.of(path.between(from,  to));
		});
	}

	List<Course> findByName(String name);
	
}
