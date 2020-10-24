package com.breakwater.task.department.dto;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class DepartmentDTO {

    UUID   id;
    String name;

    DepartmentDTO       parent;
    List<DepartmentDTO> children;

}
