package com.breakwater.task.permission.service;

import com.breakwater.task.department.dto.DepartmentDTO;
import com.breakwater.task.department.service.DepartmentService;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.permission.exception.DepartmentNotFoundException;
import com.breakwater.task.permission.exception.UserNotFoundException;
import com.breakwater.task.permission.repository.PermissionRepository;
import com.breakwater.task.user.dto.UserDTO;
import com.breakwater.task.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final DepartmentService departmentService;
    private final UserService userService;

    public Mono<String> getPermissionDescriptionByDepartmentAndUser(UUID departmentId, UUID userId) {
        Mono<DepartmentDTO> departmentDTOMono =
                departmentService
                        .readDepartmentWithParents(departmentId)
                        .switchIfEmpty(Mono.error(new DepartmentNotFoundException("Department not found ID: " + departmentId)));
        Mono<UserDTO> userDTOMono = userService.findById(userId).switchIfEmpty(Mono.error(new UserNotFoundException("User not found ID: " + userId)));
        return departmentDTOMono.flatMap(departmentDTO -> userDTOMono.flatMap(userDTO -> getPermissionDescriptionByDepartmentAndUser(departmentDTO, userDTO)));
    }

    private Mono<String> getPermissionDescriptionByDepartmentAndUser(DepartmentDTO departmentDTO, UserDTO userDTO) {
        if (departmentDTO == null) {
            return Mono.just(PermissionType.NONE.name());
        } else {
            return permissionRepository
                    .findByDepartment_IdAndUser_Id(departmentDTO.getId(), userDTO.getId())
                    .flatMap(permission -> Mono.just(permission.getType().name()))
                    .switchIfEmpty(getPermissionDescriptionByDepartmentAndUser(departmentDTO.getParent(), userDTO));
        }
    }
}
