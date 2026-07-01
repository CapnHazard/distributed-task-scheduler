package com.capnhazard.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.capnhazard.demo.enums.TaskStatus;

@Entity
public class TaskExecutionHistory {

    @ManyToOne
    private Task task;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime completedAt;
    private int retryCount;
    private TaskStatus status;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    public int getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
}
