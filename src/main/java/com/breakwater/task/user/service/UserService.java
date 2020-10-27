package com.breakwater.task.user.service;

import com.breakwater.task.user.dto.UserDTO;
import com.breakwater.task.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Mono<UserDTO> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDTO(user.getId(), user.getNickname()));
    }

}
