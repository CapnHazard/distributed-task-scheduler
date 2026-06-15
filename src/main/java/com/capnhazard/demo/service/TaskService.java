package com.capnhazard.demo.service;
import java.util.List;

import com.capnhazard.demo.enums.TaskStatus;
import com.capnhazard.demo.model.Task;
import com.capnhazard.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        if(task.getName() == null || task.getName().isEmpty()) {
            throw new RuntimeException("Exception Found- TASK HAS NO NAME");
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll(); 
    }

    public Task getTaskByID(Long id) {
        return taskRepository.findById(id).orElseThrow( () -> new RuntimeException("Task with given ID not found."));
    }

    public Task cancelTask(Long id) {
        Task t = getTaskByID(id);
        if(t.getStatus() == TaskStatus.PENDING || t.getStatus() == TaskStatus.BLOCKED) {
            t.setStatus(TaskStatus.FAILED);
            taskRepository.save(t);
        } else {
            throw new RuntimeException("Exception Found- TASK CANNOT BE CANCELLED.");
        }
        return t;
    }

}
