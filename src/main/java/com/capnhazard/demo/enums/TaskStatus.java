package com.capnhazard.demo.enums;

public enum TaskStatus {
    PENDING,
    RUNNING,
    DONE,
    FAILED,
    CANCELLED,
    BLOCKED  // waiting on a dependency
}
