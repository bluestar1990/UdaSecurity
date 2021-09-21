package com.bluestar.springboot.basics.springbootin10steps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bluestar.springboot.basics.springbootin10steps.jpa.Person;
import com.bluestar.springboot.basics.springbootin10steps.jpa.PersonJpaRepository;

@RestController
public class PersonJpaController {

	@Autowired
	PersonJpaRepository personJpaRepository;

	@GetMapping("/jpa/person/{id}")
	public Person getPersonsById(@PathVariable int id) {
		return personJpaRepository.findById(id);
	}

	@PostMapping("/jpa/person")
	public Person addNewPerson(@RequestBody Person person) {
		return personJpaRepository.insert(person);
	}

	@PutMapping("/jpa/person/{id}")
	public Person updatePerson(@RequestBody Person person, @PathVariable int id) {
		person.setId(id);
		return personJpaRepository.update(person);
	}

	@DeleteMapping("/jpa/person/{id}")
	public void deletePerson(@PathVariable int id) {
		personJpaRepository.delete(id);
	}
}
