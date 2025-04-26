package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
	/*
	 * 	Create a user

		Update a user

		Delete a user

		Get user by ID
	 */
	// 1.create user
	
	//@PostMapping → This method handles POST requests to /api/users.
	//@RequestBody → It reads the incoming JSON body and maps it to a User object.
	//ResponseEntity<User>:A wrapper to build HTTP responses with status codes like 200, 201, 404, etc.
	
	@PostMapping //hits deafulat: "/api/users"
	public ResponseEntity<User> createUser(@RequestBody User user){
		User savedUser=userService.createUser(user);
		return new ResponseEntity<User>(savedUser,HttpStatus.CREATED);
		
	}
	
	//2. Get user by ID
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id){
		User user=userService.getUserById(id);
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	//3.update user
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id,@RequestBody User updatedUser){
		User user=userService.updateUser(id, updatedUser);
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	// 4.Delete user
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id){
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//5. get all users
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> users=userService.getAllUsers();
		return new ResponseEntity<>(users,HttpStatus.OK);
	}
	
	
	
}
