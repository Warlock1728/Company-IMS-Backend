package com.bytesfarms.companyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bytesfarms.companyMain.entity.Task;
import com.bytesfarms.companyMain.entity.TimeSheet;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	Task save(Task task);

	List<Task> findByUserId(Long userId);

}
