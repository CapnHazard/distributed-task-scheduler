package com.capnhazard.demo.controller;

import java.util.List;
import com.capnhazard.demo.model.Task;
import com.capnhazard.demo.service.TaskService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        Task t = taskService.createTask(task);
        return t;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        List<Task> t = taskService.getAllTasks();
        return t;
    }
    
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        Task t = taskService.getTaskById(id);
        return t;
    }

    @DeleteMapping("/{id}/cancel")
    public Task cancelTask(@PathVariable Long id) {
        Task t = taskService.cancelTask(id);
        return t;
    }
}
