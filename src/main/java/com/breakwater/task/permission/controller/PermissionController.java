package com.breakwater.task.permission.controller;

import com.breakwater.task.permission.dto.PermissionDTO;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    public static final String PERMISSION_END_POINT_V1 = "/v1/permissions/from/department/{departmentId}/user/{userId}";


    @GetMapping(PERMISSION_END_POINT_V1)
    public Mono<ResponseEntity<String>> getPermissionByDepartmentAndUser(@PathVariable("departmentId")  UUID departmentId,
                                                                         @PathVariable("userId")        UUID userId) {
        return
                permissionService
                        .getPermissionDescriptionByDepartmentAndUser(departmentId, userId)
                        .map(description -> new ResponseEntity<>(description, HttpStatus.OK))
                        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(PERMISSION_END_POINT_V1)
    public Mono<ResponseEntity<PermissionDTO>> updatePermissionByDepartmentAndUser(@PathVariable("departmentId")  UUID departmentId,
                                                                                   @PathVariable("userId")        UUID userId,
                                                                                   @RequestBody                   String permissionType) {
        return
                permissionService
                        .updatePermissionByDepartmentAndUser(departmentId, userId, PermissionType.valueOf(permissionType.trim()))
                        .map(permission -> new ResponseEntity<>(permission, HttpStatus.OK))
                        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
