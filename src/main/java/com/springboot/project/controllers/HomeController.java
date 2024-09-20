package com.springboot.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.model.Roles;
import com.springboot.project.repositories.RolesRepository;

@RestController
public class HomeController {
	
	@Autowired
	private RolesRepository repository;
	
	@GetMapping(value="/getRoles")
	public Roles getRoles() {
		return new Roles(1, "ABC");
	}
	@GetMapping(value="/getAllRoles")
	public List<Roles> getAllRoles() {
		return repository.getAllRoles();
	}
}
