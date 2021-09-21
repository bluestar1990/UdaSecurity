package com.bluestar.springboot.basics.springbootin10steps.jpa.hibernate;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class CourseRepository {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@PersistenceContext
	EntityManager entityManager;

	public Course findById(long id) {
		return entityManager.find(Course.class, id);
	}

	public void delete(long id) {
		logger.info("Start delete Course with id=?", id);
		Course course = findById(id);
		entityManager.remove(course);
		logger.info("End delete Course with id=?", id);
	}

	public Course save(Course course) {
		if (Objects.isNull(course.getId())) {
			entityManager.persist(course);
		} else {
			entityManager.merge(course);
		}
		return course;
	}
	
	public void playWithEntityManager() {
		Course course = new Course("Web Services in 100 Steps");
		entityManager.merge(course); // Persistence Context
		course.setName("Web Services in 100 Steps - Update");
	}
	
	// Persist dùng để chuyển một entity từ trạng thái transitent sang persistence(persistence entity) 
	//được quản lý bởi Hibernate. Vì vậy persist chỉ được sử dụng với transitent entity 
	//(một thực thể mới không có quan hệ với một dòng dữ liệu nào dưới database).
	//flush-time là thời điểm mà JPA-Hibernate đồng bộ hoá các thay đổi của các entity xuống database.
	// trong Hibernate và JPA sử dụng mặc định flush-before-query
	//Merge dùng để chuyển một entity từ trạng thái detached (detached entity) sang persistence 
	// https://shareprogramming.net/persist-va-merge-lam-viec-nhu-the-nao-trong-jpa/
}
