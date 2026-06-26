package com.capnhazard.demo.repository;

import com.capnhazard.demo.model.Task;
import com.capnhazard.demo.enums.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusAndScheduledAtLessThanEqual(TaskStatus status, LocalDateTime time);
}
