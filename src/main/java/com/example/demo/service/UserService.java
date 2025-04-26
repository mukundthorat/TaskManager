package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	/*
	 * 	Create a user

		Update a user

		Delete a user

		Get user by ID
	 */
	//create user
	public User createUser(User user) {
		return userRepository.save(user);
	}
	
	//update user,updatedUser is the object built from JSON request body
	public User updateUser(Long id,User updatedUser) {
		Optional<User> optionalUser=userRepository.findById(id);
		
		if(optionalUser.isPresent()) {
			User existingUser=optionalUser.get();
			existingUser.setFirstName(updatedUser.getFirstName());
			existingUser.setLastName(updatedUser.getLastName());
			existingUser.setTimezone(updatedUser.getTimezone());
			existingUser.setIsActive(updatedUser.getIsActive());
			
			return userRepository.save(existingUser);
		} else {
			throw new RuntimeException("User not found with id: " + id);
		}
	}
	
	//Delete user
	public void deleteUser(Long id) {
		if(userRepository.existsById(id)) {
			userRepository.deleteById(id);
		} else {
			throw new RuntimeException("User not found with id: " + id);
		}
	}
	
	//get user by ID(for use in taskservice later)
	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("User not found with id: " + id));
	}
	
	//get all users
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}

}
