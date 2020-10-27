package com.breakwater.task.permission.service;

import com.breakwater.task.department.model.Department;
import com.breakwater.task.department.repository.DepartmentRepository;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.permission.exception.DepartmentNotFoundException;
import com.breakwater.task.permission.exception.UserNotFoundException;
import com.breakwater.task.permission.repository.PermissionRepository;
import com.breakwater.task.user.model.User;
import com.breakwater.task.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public Mono<String> getPermissionDescriptionByDepartmentAndUser(UUID departmentId, UUID userId) {
        Mono<Department> department = departmentRepository.findById(departmentId)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException("Department not found ID: " + departmentId)));
        Mono<User> user = userRepository.findById(userId).switchIfEmpty(Mono.error(new UserNotFoundException("User not found ID: " + userId)));
        return permissionRepository
                .findByDepartmentAndUser(department, user)
                .flatMap(permission -> Mono.just(permission.getType().name()))
                .defaultIfEmpty(PermissionType.NONE.name());
    }
}
