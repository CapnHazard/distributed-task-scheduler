package com.capnhazard.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import com.capnhazard.demo.enums.TaskStatus;
import com.capnhazard.demo.model.Task;
import com.capnhazard.demo.model.TaskExecutionHistory;
import com.capnhazard.demo.repository.TaskExecutionHistoryRepository;
import com.capnhazard.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import jakarta.persistence.OptimisticLockException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Service
public class TaskService {

    private final TaskExecutionHistoryRepository taskLog;
    private final TaskRepository taskRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    public TaskService(TaskRepository taskRepository, ThreadPoolTaskExecutor taskExecutor, TaskExecutionHistoryRepository taskLog) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
        this.taskLog = taskLog;
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
            try {
                t.setCompletedAt(LocalDateTime.now());
                t = taskRepository.save(t);

                TaskExecutionHistory history = new TaskExecutionHistory();
                history.setTask(t);
                history.setCompletedAt(t.getCompletedAt());
                history.setRetryCount(t.getRetryCount());
                history.setStatus(t.getStatus());
                this.taskLog.save(history);

                // dependency resolution: unblock tasks waiting on this task
                List<Task> TaskList;
                TaskList = taskRepository.findByStatusAndDependsOn(TaskStatus.BLOCKED, t.getId());
                for(Task task : TaskList) {
                    task.setStatus(TaskStatus.PENDING);
                    task = taskRepository.save(task);
                }
                
            } catch(OptimisticLockException e) {
                System.out.println("Version conflict while saving DONE status for task ID: " + t.getId()
                + ". Another thread may have modified this task.");
                
                // version conflict on DONE-save: re-fetch current version and reapply
                Long taskID = t.getId();
                Task k = taskRepository.findById(taskID).orElseThrow(
                () -> new ResponseStatusException (HttpStatus.NOT_FOUND, "Task not found with id: " + taskID));
                k.setStatus(TaskStatus.DONE);
                taskRepository.save(k);
            }

        } catch (Exception e) {
            int retryCount = t.getRetryCount();
            int maxRetries = t.getMaxRetries();
            if(retryCount < maxRetries) {
                t.setStatus(TaskStatus.PENDING);
                t.setRetryCount(++retryCount);
                t.setScheduledAt(LocalDateTime.now().plusSeconds((long) Math.pow(2, retryCount))); //retry logic
                try {
                    t = taskRepository.save(t);
                } catch(OptimisticLockException x) {
                    System.out.println("Version conflict while saving retry (PENDING) status for task ID: " 
                    + t.getId() + ", retryCount: " + retryCount);
                }
                
            } else {
                t.setStatus(TaskStatus.FAILED);
                try {
                    t = taskRepository.save(t);
                } catch(OptimisticLockException y) {
                    System.out.println("Version conflict while saving FAILED status for task ID: " 
                    + t.getId());
                }
            }
        }
    }
}
