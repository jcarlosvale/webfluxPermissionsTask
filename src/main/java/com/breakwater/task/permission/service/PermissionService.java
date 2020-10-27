package com.breakwater.task.permission.service;

import com.breakwater.task.department.dto.DepartmentDTO;
import com.breakwater.task.department.model.Department;
import com.breakwater.task.department.service.DepartmentService;
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
    private final DepartmentService departmentService;
    private final UserRepository userRepository;

    public Mono<String> getPermissionDescriptionByDepartmentAndUser(UUID departmentId, UUID userId) {
        Mono<DepartmentDTO> departmentDTOMono =
                departmentService
                        .readDepartmentWithParents(departmentId)
                        .switchIfEmpty(Mono.error(new DepartmentNotFoundException("Department not found ID: " + departmentId)));
        Mono<User> userMono = userRepository.findById(userId).switchIfEmpty(Mono.error(new UserNotFoundException("User not found ID: " + userId)));
        return getPermissionDescriptionByDepartmentAndUser(departmentDTOMono, userMono).log();
    }

    private Mono<String> getPermissionDescriptionByDepartmentAndUser(Mono<DepartmentDTO> departmentDTOMono, Mono<User> userMono) {
        return permissionRepository
                .findByDepartment_IdAndUser_Id(departmentDTOMono.flatMap(departmentDTO -> Mono.just(departmentDTO.getId())), userMono.flatMap(user1 -> Mono.just(user1.getId())))
                .flatMap(permission -> Mono.just(permission.getType().name()))
                .switchIfEmpty(Mono.just(PermissionType.NONE.name()));

//        return
//        departmentDTOMono
//                .flatMap(departmentDTO -> {
//                    return
//                            permissionRepository
//                                    .findByDepartment_IdAndUser_Id(Mono.just(departmentDTO.getId()), userMono.flatMap(user1 -> Mono.just(user1.getId())))
//                                    .flatMap(permission -> Mono.just(permission.getType().name()))
//                                    .switchIfEmpty(Mono.just(PermissionType.NONE.name()));

//                    if (departmentDTO.getParent() != null) {
//                        return
//                        permissionRepository
//                                .findByDepartment_IdAndUser_Id(Mono.just(departmentDTO.getId()), userMono.flatMap(user1 -> Mono.just(user1.getId())))
//                                .flatMap(permission -> Mono.just(permission.getType().name()))
//                                .switchIfEmpty(getPermissionDescriptionByDepartmentAndUser(Mono.just(departmentDTO.getParent()), userMono));
//                    } else { //root
//                        return
//                        permissionRepository
//                                .findByDepartment_IdAndUser_Id(Mono.just(departmentDTO.getId()), userMono.flatMap(user1 -> Mono.just(user1.getId())))
//                                .flatMap(permission -> Mono.just(permission.getType().name()))
//                                .switchIfEmpty(Mono.just(PermissionType.NONE.name()));
//                    }
//                });
    }
}
