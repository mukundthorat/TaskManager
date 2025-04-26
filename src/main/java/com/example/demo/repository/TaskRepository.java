package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
	//To check for duplicate title + endDateTime for a createdBy user
	
	//Derived Query Method
	//Spring Data JPA allows you to define query methods by just writing method names that follow a specific naming convention 
	Optional<Task> findByTitleAndExpectedEndDateTimeAndCreatedBy(String title,LocalDateTime endDate,User createdBy);
	
	// Page through non-deleted tasks
	Page<Task> findByIsDeleteFalse(Pageable peageable);
	
	// Filter: status
    Page<Task> findByIsDeleteFalseAndStatus(TaskStatus status, Pageable pageable);

    // Filter: assignedTo
    Page<Task> findByIsDeleteFalseAndAssignedTo_Id(Long assignedToId, Pageable pageable);

    // Filter: both
    Page<Task> findByIsDeleteFalseAndStatusAndAssignedTo_Id(TaskStatus status, Long assignedToId, Pageable pageable);

	
}
