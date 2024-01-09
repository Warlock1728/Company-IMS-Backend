package com.bytesfarms.companyMain.serviceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytesfarms.companyMain.dto.TaskDTO;
import com.bytesfarms.companyMain.entity.Task;
import com.bytesfarms.companyMain.entity.User;
import com.bytesfarms.companyMain.repository.TaskRepository;
import com.bytesfarms.companyMain.repository.UserRepository;
import com.bytesfarms.companyMain.service.TaskService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Task createTask(Long userId, TaskDTO taskDTO) {

		Task task = new Task();

		task.setTaskDescription(taskDTO.getTaskDescription());
		task.setExpectedTime(LocalTime.parse(taskDTO.getExpectedTime(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		task.setActualTime(LocalTime.parse(taskDTO.getActualTime(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		task.setStatus(taskDTO.getStatus());
		task.setDate(LocalDate.now());
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

		task.setUser(user);
		return taskRepository.save(task);

	}

	// Get all tasks for that employee.
	@Override
	public List<Task> getTasksByUserId(Long userId) {
		if (userId == 0) {

			return taskRepository.findAll();
		} else {

			return taskRepository.findByUserId(userId);
		}
	}

	@Override
	public Task updateTask(Long userId, Long taskId, TaskDTO taskDTO) {
		Task existingTask = taskRepository.findById(taskId)
				.orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

		if (taskDTO.getTaskDescription() != null) {
			existingTask.setTaskDescription(taskDTO.getTaskDescription());
		}

		if (taskDTO.getExpectedTime() != null) {
			existingTask.setExpectedTime(
					LocalTime.parse(taskDTO.getExpectedTime(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		}

		if (taskDTO.getActualTime() != null) {
			existingTask
					.setActualTime(LocalTime.parse(taskDTO.getActualTime(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		}

		if (taskDTO.getStatus() != null) {
			existingTask.setStatus(taskDTO.getStatus());
		}

		return taskRepository.save(existingTask); // Update a task for employee if he needs any changes
	}

}
