package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data	//for getters and setters
@NoArgsConstructor
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot be more than 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Title must not contain special characters")
    @Column(nullable = false)
	private String title;
	
	@NotBlank(message = "Description cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9 ,\\.\\-]+$", message = "Description must not contain special characters")@Column(nullable = false)
	private String description;
	
	@NotNull(message = "Status is mandatory")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskStatus status;
	
	@Column(nullable = true)
	private LocalDateTime expectedStartDateTime;
	@Column(nullable = true)
	private LocalDateTime expectedEndDateTime;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	//flag to delete
	@Column(nullable = false)
	private Boolean isDelete=false;
	
	//connecting two tables
	@ManyToOne(optional = false)
	@JoinColumn(name="created_by_id",nullable = false)
	@NotNull(message = "CreatedBy is required")
	private User createdBy;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="assigned_to_id",nullable = false)
	@NotNull(message = "AssignedTo is required")
	private User assignedTo;
	
	
	
	
	
	

}
