package com.breakwater.task.permission.service;

import com.breakwater.task.department.dto.DepartmentDTO;
import com.breakwater.task.department.service.DepartmentService;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.permission.exception.DepartmentNotFoundException;
import com.breakwater.task.permission.exception.UserNotFoundException;
import com.breakwater.task.permission.model.Permission;
import com.breakwater.task.permission.repository.PermissionRepository;
import com.breakwater.task.user.model.User;
import com.breakwater.task.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
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
        return departmentDTOMono.flatMap(departmentDTO -> {
            return userMono.flatMap(
                    user -> {
                        return getPermissionDescriptionByDepartmentAndUser(departmentDTO, user);
                    });
                });

    }

    private Mono<String> getPermissionDescriptionByDepartmentAndUser(DepartmentDTO departmentDTOMono, User userMono) {
        log.info("EXECUTANDO 2");
        if (departmentDTOMono == null) {
            return Mono.just(PermissionType.NONE.name());
        } else {
            return permissionRepository
                    .findByDepartment_IdAndUser_Id(departmentDTOMono.getId(), userMono.getId())
                    .flatMap(permission -> Mono.just(permission.getType().name()))
                    .switchIfEmpty(getPermissionDescriptionByDepartmentAndUser(departmentDTOMono.getParent(), userMono));
        }
    }

    private Mono<String> getPermissionDescriptionByDepartmentAndUser(Mono<DepartmentDTO> departmentDTOMono, Mono<User> userMono) {
        log.info("EXECUTANDO");
        Mono<UUID> departmentID = departmentDTOMono.flatMap(departmentDTO -> Mono.just(departmentDTO.getId()));
        Mono<UUID> userID = userMono.flatMap(user -> Mono.just(user.getId()));

        return
        permissionRepository
                .findByDepartment_IdAndUser_Id(departmentID, userID)
                .switchIfEmpty(
                        departmentDTOMono.flatMap(
                                departmentDTO -> {
                                    if (departmentDTO.getParent() == null) {
                                        return Mono.just(new Permission(null, PermissionType.NONE, null, null));
                                    } else {
                                        Mono<String> some = getPermissionDescriptionByDepartmentAndUser(Mono.just(departmentDTO.getParent()), userMono);
                                        return some.flatMap(s -> Mono.just(new Permission(null, PermissionType.valueOf(s), null, null)));
                                    }
                                }
                        )
                )
                .flatMap(permission -> Mono.just(permission.getType().name()));

/*
        return
        departmentDTOMono.flatMap(
                departmentDTO -> {
                    if (departmentDTO.getParent() == null) {
                    //    return Mono.just("RAIZ");
                        return
                        permissionRepository
                                .findByDepartment_IdAndUser_Id(departmentID, userID)
                                .defaultIfEmpty(new Permission(null, PermissionType.NONE, null, null))
                                .flatMap(permission -> Mono.just(permission.getType().name()));
                                //.switchIfEmpty(Mono.just(PermissionType.NONE.name()));
                    } else {
                        //return getPermissionDescriptionByDepartmentAndUser(Mono.just(departmentDTO.getParent()), userMono);
                        return
                        permissionRepository
                                .findByDepartment_IdAndUser_Id(departmentID, userID)
                                .flatMap(permission -> Mono.just(permission.getType().name()))
                                .switchIfEmpty(getPermissionDescriptionByDepartmentAndUser(Mono.just(departmentDTO.getParent()), userMono));
                    }
                });
/*
        return permissionRepository
                .findByDepartment_IdAndUser_Id(departmentDTOMono.flatMap(departmentDTO -> Mono.just(departmentDTO.getId())), userMono.flatMap(user1 -> Mono.just(user1.getId())))
                .flatMap(permission -> Mono.just(permission.getType().name()))
                .switchIfEmpty(
                        departmentDTOMono.flatMap(departmentDTO -> {
                            if (departmentDTO.getParent() == null) {
                                return Mono.just("RAIZ");
                            } else {
                                return getPermissionDescriptionByDepartmentAndUser(Mono.just(departmentDTO.getParent()), userMono);
                            }
                        })
                );
*/
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
