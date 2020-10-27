package com.breakwater.task.permission.dto;

import com.breakwater.task.department.dto.DepartmentDTO;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.user.dto.UserDTO;
import lombok.Value;

import java.util.UUID;

@Value
public class PermissionDTO {
    UUID id;
    PermissionType type;

    UserDTO userDTO;
    DepartmentDTO departmentDTO;
}
