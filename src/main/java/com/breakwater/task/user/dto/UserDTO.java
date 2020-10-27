package com.breakwater.task.user.dto;

import lombok.Value;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Value
public class UserDTO {
    @Id
    UUID id;

    String nickname;
}
