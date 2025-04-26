package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User createdBy;
    private User assignedTo;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        createdBy = new User();
        createdBy.setId(1L);
        createdBy.setFirstName("Mukund");
        createdBy.setLastName("Thorat");
        createdBy.setTimezone("Asia/Delhi");
        createdBy.setIsActive(true);

        assignedTo = new User();
        assignedTo.setId(2L);
        assignedTo.setFirstName("Harish");
        assignedTo.setLastName("Dhabale");
        assignedTo.setTimezone("Asia/Kolkata");
        assignedTo.setIsActive(true);

        sampleTask = new Task();
        sampleTask.setTitle("Sample Task");
        sampleTask.setDescription("Sample description");
        sampleTask.setStatus(TaskStatus.PENDING);
        sampleTask.setCreatedBy(createdBy);
        sampleTask.setAssignedTo(assignedTo);
    }

    @Test
    void testCreateTask() {
        when(userService.getUserById(anyLong())).thenReturn(createdBy);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.createTask(sampleTask);

        assertNotNull(result);
        assertEquals("Sample Task", result.getTitle());
    }

    @Test
    void testGetTasks_WithPagination() {
        Task t1 = new Task(); t1.setTitle("T1");
        Task t2 = new Task(); t2.setTitle("T2");
        List<Task> list = Arrays.asList(t1, t2);
        Page<Task> taskPage = new PageImpl<>(list);

        when(taskRepository.findByIsDeleteFalse(any(Pageable.class))).thenReturn(taskPage);

        Page<Task> result = taskService.getTasks(PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testUpdateTask_Success() {
        User createdBy = new User();
        createdBy.setId(4L);
        User assignedTo = new User();
        assignedTo.setId(4L);

        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setDescription("Old description");
        existing.setStatus(TaskStatus.PENDING);
        existing.setCreatedBy(createdBy);
        existing.setAssignedTo(assignedTo);

        Task updated = new Task();
        updated.setTitle("Updated Title");
        updated.setDescription("Updated description");
        updated.setStatus(TaskStatus.IN_PROGRESS);
        updated.setExpectedStartDateTime(LocalDateTime.of(2025, 4, 26, 9, 0));
        updated.setExpectedEndDateTime(LocalDateTime.of(2025, 4, 26, 17, 0));
        updated.setCreatedBy(createdBy);
        updated.setAssignedTo(assignedTo);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        Task result = taskService.updateTask(1L, updated);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void testUpdateTask_WithoutCreatedBy_ShouldThrow() {
        Task updated = new Task();
        updated.setStatus(TaskStatus.IN_PROGRESS);
        updated.setAssignedTo(assignedTo);
        updated.setExpectedStartDateTime(LocalDateTime.now());
        updated.setExpectedEndDateTime(LocalDateTime.now().plusHours(1));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(updated));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> taskService.updateTask(1L, updated));

        assertTrue(ex.getMessage().contains("createdBy"));
    }

    @Test
    void testUpdateTask_MissingTimestampsForInProgress() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setCreatedBy(createdBy);
        existing.setAssignedTo(assignedTo);
        existing.setStatus(TaskStatus.PENDING);

        Task updated = new Task();
        updated.setStatus(TaskStatus.IN_PROGRESS);
        updated.setCreatedBy(createdBy);
        updated.setAssignedTo(assignedTo);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> taskService.updateTask(1L, updated));

        assertTrue(ex.getMessage().contains("Start and End datetime"));
    }

    @Test
    void testDeleteTask_Success() {
        Task existing = new Task();
        existing.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        taskService.deleteTask(1L);

        assertTrue(existing.getIsDelete());
        verify(taskRepository).save(existing);
    }
}
