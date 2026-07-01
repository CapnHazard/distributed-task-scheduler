package com.capnhazard.demo.repository;

import com.capnhazard.demo.model.TaskExecutionHistory;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TaskExecutionHistoryRepository extends JpaRepository<TaskExecutionHistory, Long>{
    
}
