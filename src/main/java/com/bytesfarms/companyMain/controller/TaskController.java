package com.bytesfarms.companyMain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytesfarms.companyMain.dto.TaskDTO;
import com.bytesfarms.companyMain.entity.Task;
import com.bytesfarms.companyMain.service.TaskService;

//Controller to add tasks , get and update a task against User Id//Employee Id.

/*
 * @author Shivendra Singh
 */

@RestController
@RequestMapping("/tasks")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@PostMapping("/create")
	public ResponseEntity<String> createTask(@RequestParam Long userId,@RequestBody TaskDTO taskDTO) {
		Task createdTask = taskService.createTask(userId,taskDTO);
		return new ResponseEntity<>("Task created successfully with ID: " + createdTask.getId(), HttpStatus.OK);
	}

	@GetMapping("/get")
	public ResponseEntity<List<Task>> getTasksByUserId(@RequestParam Long userId) {
		List<Task> tasks = taskService.getTasksByUserId(userId);
		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateTask(@RequestParam Long taskId, @RequestParam Long userId, @RequestBody TaskDTO taskDTO) {
		Task updatedTask = taskService.updateTask(userId,taskId, taskDTO);
		return new ResponseEntity<>("Task with ID " + taskId + " updated successfully", HttpStatus.OK);
	}
}
