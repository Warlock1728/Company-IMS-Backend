package com.bytesfarms.companyMain.service;

import java.util.List;

import com.bytesfarms.companyMain.dto.TaskDTO;
import com.bytesfarms.companyMain.entity.Task;

public interface TaskService {

	

	

	Task updateTask(Long userId,Long taskId, TaskDTO taskDTO);

	Task createTask(Long userId, TaskDTO taskDTO);

	List<Task> getTasksByUserIdAndDate(Long userId, String date);

	String deleteTask(Long taskId);
	

}
