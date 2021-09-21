//package com.bluestar.springboot.basics.springbootin10steps.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.bluestar.springboot.basics.springbootin10steps.jdbc.Person;
//import com.bluestar.springboot.basics.springbootin10steps.jdbc.PersonJdbcDAO;
//
//@RestController
//public class PersonController {
//
//	@Autowired
//	private PersonJdbcDAO personJdbcDAO;
//
//	@GetMapping("/persons")
//	public List<Person> getAllPersons() {
//		return personJdbcDAO.findAll();
//	}
//
//	@GetMapping("/person/{id}")
//	public Person getPersonsById(@PathVariable int id) {
//		return personJdbcDAO.findById(id);
//	}
//
//	@DeleteMapping("/person/{id}")
//	public int deletePersonsById(@PathVariable int id) {
//		return personJdbcDAO.deleteById(id);
//	}
//
//	@PostMapping("/person")
//	public int addPerson(@RequestBody Person person) {
//		return personJdbcDAO.insert(person);
//	}
//	
//	@PutMapping("/person/{id}")
//	public int updatePerson(@RequestBody Person person, @PathVariable int id) {
//		person.setId(id);
//		return personJdbcDAO.update(person);
//	}
//}
