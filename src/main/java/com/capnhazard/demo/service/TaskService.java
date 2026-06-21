package com.capnhazard.demo.service;
import java.util.List;

import com.capnhazard.demo.enums.TaskStatus;
import com.capnhazard.demo.model.Task;
import com.capnhazard.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        if(task.getName() == null || task.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception Found- TASK HAS NO NAME");
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll(); 
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }

    public Task cancelTask(Long id) {
        Task t = getTaskById(id);
        if(t.getStatus() == TaskStatus.PENDING || t.getStatus() == TaskStatus.BLOCKED) {
            t.setStatus(TaskStatus.FAILED);
            taskRepository.save(t);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Exception Found- TASK CANNOT BE CANCELLED");
        }
        return t;
    }

}
