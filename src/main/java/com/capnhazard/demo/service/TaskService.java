package com.capnhazard.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import com.capnhazard.demo.config.TaskExecutorConfig;
import com.capnhazard.demo.enums.TaskStatus;
import com.capnhazard.demo.model.Task;
import com.capnhazard.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import jakarta.persistence.OptimisticLockException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    public TaskService(TaskRepository taskRepository, ThreadPoolTaskExecutor taskExecutor) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
    }

    public Task createTask(Task task) {
        if(task.getName() == null || task.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception Found- TASK HAS NO NAME");
        }
        if(task.getScheduledAt() == null) {
            task.setScheduledAt(LocalDateTime.now());
        } else if(LocalDateTime.now().minusSeconds(60).isAfter(task.getScheduledAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception Found- TASK SCHEDULED IN THE PAST");
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
            t.setStatus(TaskStatus.CANCELLED);
            taskRepository.save(t);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Exception Found- TASK CANNOT BE CANCELLED");
        }
        return t;
    }

    public List<Task> getDueTasks() {
        return taskRepository.findByStatusAndScheduledAtLessThanEqual(TaskStatus.PENDING, LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 5000)
    public void dispatchTasks() {
        List<Task> tasks = getDueTasks();
        for(Task t : tasks) {
            try {
                t.setStatus(TaskStatus.RUNNING);
                t = taskRepository.save(t);
                Task x = t;
                taskExecutor.execute( () -> executeTasks(x));
            } catch(OptimisticLockException e) {
                System.out.println("Exception Found- TASK ALREADY RUNNING. " + e.getMessage());
            }
        }
    }

    public void executeTasks(Task t) {
        try {
            Thread.sleep(2000);
            t.setStatus(TaskStatus.DONE);
            t = taskRepository.save(t);
        } catch (Exception e) {
            t.setStatus(TaskStatus.FAILED);
            t = taskRepository.save(t);
        }
    }
}
