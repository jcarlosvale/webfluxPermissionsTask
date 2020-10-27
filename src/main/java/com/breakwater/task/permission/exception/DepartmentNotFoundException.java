package com.breakwater.task.permission.exception;

public class DepartmentNotFoundException extends RuntimeException{
    public DepartmentNotFoundException(String msg) {
        super(msg);
    }
}
