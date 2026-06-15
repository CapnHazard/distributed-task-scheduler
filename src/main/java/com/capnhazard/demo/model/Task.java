package com.capnhazard.demo.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.capnhazard.demo.enums.TaskPriority;
import com.capnhazard.demo.enums.TaskStatus;

@Entity
@Table(name = "tasks") 
public class Task {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false) 
    private String name;
    private String description;

    @Column(nullable = false) @Enumerated(EnumType.STRING) private TaskStatus status;
    @Column(nullable = false) @Enumerated(EnumType.STRING) private TaskPriority priority;

    @Column(nullable = false) private LocalDateTime scheduledAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private int retryCount;
    private int maxRetries;

    @Version 
    private Long version; // Used by JPA for optimistic locking — do not modify manually.
    private Long dependsOn; // ID of the task this task is waiting on. Null = no dependency.

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = TaskStatus.PENDING;
        if (this.priority == null) this.priority = TaskPriority.MEDIUM;
        if (this.retryCount == 0) this.retryCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(Long dependsOn) {
        this.dependsOn = dependsOn;
    }
}