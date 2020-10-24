package com.breakwater.task.user.model;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Value
@Document
public class User {

    @Id
    UUID id;

    String nickname;

}
