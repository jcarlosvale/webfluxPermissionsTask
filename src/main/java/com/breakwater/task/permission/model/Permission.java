package com.breakwater.task.permission.model;

import com.breakwater.task.department.model.Department;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.user.model.User;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Document
public class Permission {
    PermissionType type;

    User user;
    Department department;
}
