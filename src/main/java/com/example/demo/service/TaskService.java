package com.example.demo.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private UserService userService;
	
	//1.create task with Validations (non duplicate task,(same user +same endtime))
	public Task createTask(Task task) {
		
		if (task.getCreatedBy() == null || task.getCreatedBy().getId() == null) {
	        throw new RuntimeException("createdBy: CreatedBy is required");
	    }

	    if (task.getAssignedTo() == null || task.getAssignedTo().getId() == null) {
	        throw new RuntimeException("assignedTo: AssignedTo is required");
	    }
		
		
		//fetch and validate users
		User createdBy=userService.getUserById(task.getCreatedBy().getId());//manager
		User assignedTo=userService.getUserById(task.getAssignedTo().getId());//employee
		
		task.setCreatedBy(createdBy);
		task.setAssignedTo(assignedTo);
		
		//CHECK
		// Check for duplicate title + endDateTime for same createdBy
		//lets say existing
		Optional<Task> existing=taskRepository.findByTitleAndExpectedEndDateTimeAndCreatedBy
				(
				task.getTitle(), 
				task.getExpectedEndDateTime(), 
				createdBy
		);
		//if yes then
		if(existing.isPresent()) {
			throw new RuntimeException("A task with the same title and deadline already exists for this user.");
		}
		
		// check timestamps if IN_PROGRESS
		if(task.getStatus()==TaskStatus.IN_PROGRESS) {
			if(task.getExpectedStartDateTime() == null || task.getExpectedEndDateTime() ==null) {
				throw new RuntimeException("Start and End datetime must be provided for IN_PROGRESS tasks.");
			}
		}
		
		// Set timestamps UTC
		task.setCreatedAt(java.time.LocalDateTime.now());
		task.setUpdatedAt(java.time.LocalDateTime.now());
		
		//save task
		return taskRepository.save(task);
	}
	
	
	// 2. Get Task By ID
	public Task getTaskById(Long id) {
		return taskRepository.findById(id)		//find by id handles error properly
				.orElseThrow(()-> new RuntimeException("Task not found with id: " + id));
	}
	
	// 3. Get All Tasks(non deleted)
	public List<Task> getAllTasks(){
		return taskRepository.findAll()
				.stream()
				.filter(task-> !Boolean.TRUE.equals(task.getIsDelete()))// Skip soft-deleted
				.toList();
	}
	
	//4.soft delete task
	public void deleteTask(Long id) {
		Task task =getTaskById(id);
		task.setIsDelete(true);
		taskRepository.save(task);	//update instead of delete
	}
	
	//5.Get All Deleted tasks
	public List<Task> getAllDeletedTasks(){
		return taskRepository.findAll()
				.stream()
				.filter(task-> Boolean.TRUE.equals(task.getIsDelete()))
				.toList();
	}
	
	//6.Update task
	@org.springframework.transaction.annotation.Transactional
	public Task updateTask(Long id,Task updatedTask) {
		System.out.println("Updating Task ID: " + id);
		Task task=getTaskById(id);
		
		//to prevent a task from updation if it is softdeleted
		if(Boolean.TRUE.equals(task.getIsDelete())) {
			throw new RuntimeException("Cannot update a deleted task.");
		}
		
		if (updatedTask.getCreatedBy() == null || updatedTask.getCreatedBy().getId() == null) {
	        throw new RuntimeException("createdBy: CreatedBy is required");
	    }

	    if (updatedTask.getAssignedTo() == null || updatedTask.getAssignedTo().getId() == null) {
	        throw new RuntimeException("assignedTo: AssignedTo is required");
	    }
		
		task.setTitle(updatedTask.getTitle());
		task.setDescription(updatedTask.getDescription());
		task.setStatus(updatedTask.getStatus());
		
		//also need to update these fields
		User createdBy=userService.getUserById(updatedTask.getCreatedBy().getId());
		User assignedTo=userService.getUserById(updatedTask.getAssignedTo().getId());
		task.setCreatedBy(createdBy);
		task.setAssignedTo(assignedTo);
		
		//the following line will update if TaskStaus ==IN_PROGRESS 
		if(task.getStatus()==TaskStatus.IN_PROGRESS) {
			if(updatedTask.getExpectedStartDateTime() ==null || updatedTask.getExpectedEndDateTime() == null) {
				throw new RuntimeException("Start and End datetime must be provided for IN_PROGRESS tasks.");
			}
			task.setExpectedStartDateTime(updatedTask.getExpectedStartDateTime());
			task.setExpectedEndDateTime(updatedTask.getExpectedEndDateTime());
		}
		
		
		task.setUpdatedAt(LocalDateTime.now());
		
		
		System.out.println("Saving task: " + task.getTitle() +
			    " | Start: " + task.getExpectedStartDateTime() +
			    " | End: " + task.getExpectedEndDateTime() +
			    " | CreatedBy ID: " + task.getCreatedBy().getId() +
			    " | AssignedTo ID: " + task.getAssignedTo().getId()
			);
		try{return taskRepository.save(task);} catch(Exception e) {
			e.printStackTrace(); // This will print full Hibernate/Postgres error
	        throw new RuntimeException("Error while saving task: " + e.getMessage());
		}
	}
	
	//Retrieve tasks in a page-wise fashion, skipping soft-deleted.
	public Page<Task> getTasks(Pageable pageable) {
		return taskRepository.findByIsDeleteFalse(pageable);
	}
	
	
	// A logic based on scenarios of filtering and sorting
	public Page<Task> getTasks(TaskStatus status,Long assignedToId, Pageable pageable){
		if(status!=null &&  assignedToId !=null) {
			return taskRepository.findByIsDeleteFalseAndStatusAndAssignedTo_Id(status, assignedToId, pageable);
		} else if (status != null) {
            return taskRepository.findByIsDeleteFalseAndStatus(status, pageable);
        } else if (assignedToId != null) {
            return taskRepository.findByIsDeleteFalseAndAssignedTo_Id(assignedToId, pageable);
        } else {
            return taskRepository.findByIsDeleteFalse(pageable);
        }
	}

}









