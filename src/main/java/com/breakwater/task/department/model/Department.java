package com.breakwater.task.department.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Value
@Document
public class Department {

    @Id
    UUID id;

    String name;
    UUID   parentId;

}
