package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	@Autowired
	private TaskService taskService;
	
	//1. Create Task
	@PostMapping
	public ResponseEntity<Task> createTask(@RequestBody Task task){
		Task createdTask=taskService.createTask(task);
		return new ResponseEntity<>(createdTask,HttpStatus.CREATED);
	}
	
	// 1.1 Get all tasks, non-deleted, paginated, with optional filtering and sorting
	
	
	
	//2 get all tasks ,non deleted,paginated,with optional filtering and sorting
	@GetMapping
	public ResponseEntity<Page<Task>> getAllTasks
			(
				@RequestParam(defaultValue = "0") int page,
				@RequestParam(defaultValue = "11") int size,
				@RequestParam(required = false) TaskStatus status,
				@RequestParam(required = false) Long assignedToId,
		        @RequestParam(defaultValue = "createdAt,desc") String sort
				){
		//parse
		String[] sortParams=sort.split(",");
		Sort.Direction direction=Sort.Direction.fromOptionalString(sortParams.length>1? sortParams[1]:"asc")
				.orElse(Sort.Direction.ASC);
		String sortField=sortParams[0];
		
		
		
		Pageable pageable=PageRequest.of(page,size,Sort.by(direction, sortField));
		
		Page<Task> pageResult=taskService.getTasks(status, assignedToId, pageable);
		return new ResponseEntity<>(pageResult,HttpStatus.OK);
	}
	
	// 3. Get task by ID
	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable Long id){
		Task task=taskService.getTaskById(id);
		return new ResponseEntity<>(task,HttpStatus.OK);
	}
	
	//4 update task
	@PutMapping("/{id}")
	public ResponseEntity<Task> updateTask(@PathVariable Long id,@RequestBody Task updatedTask){
		Task task=taskService.updateTask(id, updatedTask);
		return new ResponseEntity<>(task,HttpStatus.OK);
	}
	
	// 5. Soft delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
    	taskService.deleteTask(id);
    	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // 6.get all deleted tasks
    @GetMapping("/deletedTask/all")
    public ResponseEntity<List<Task>> getAllDeletedTasks(){
    	List<Task> deletedTasks=taskService.getAllDeletedTasks();
    	return new ResponseEntity<>(deletedTasks,HttpStatus.OK);
    }
	
	
	
}









