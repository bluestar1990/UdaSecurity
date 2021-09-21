package com.bluestar.springboot.basics.springbootin10steps.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bluestar.springboot.basics.springbootin10steps.Book;

@RestController
public class BookController {

	@GetMapping("/book")
	public List<Book> getAll() {
		return Arrays.asList(new Book(1l, "Master SpringBoot", "phucvv31"), new Book(2, "Master JPA", "phucvv3"));
	}
}
