package com.bluestar.springboot.basics.springbootin10steps.jpa.hibernate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
public class CourseController {

	@Autowired
	CourseRepository courseRepository;

	@GetMapping("jpa-hibernate/course/{id}")
	public Course findCoursById(@PathVariable long id) {
		return courseRepository.findById(id);
	}

	@DeleteMapping("jpa-hibernate/course/{id}")
	public void deleteCoursById(@PathVariable long id) {
		courseRepository.delete(id);
	}

	@PostMapping("/jpa-hibernate/course")
	public Course addNewCourse(@RequestBody Course course) {
		return courseRepository.save(course);
	}

	@PutMapping("/jpa-hibernate/course/{id}")
	public Course updatePerson(@RequestBody Course course) {
		return courseRepository.save(course);
	}

}
