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
@RequestMapping("/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    private static final String PERMISSION_QUERY_END_POINT_V1 = "/from";
    private static final String DEPARTMENT_RESOURCE = "/department";
    private static final String USER_RESOURCE = "/user";


    @GetMapping(PERMISSION_QUERY_END_POINT_V1 + DEPARTMENT_RESOURCE + "/{departmentId}" + USER_RESOURCE + "/{userId}")
    public Mono<ResponseEntity<String>> getPermissionByDepartmentAndUser(@PathVariable("departmentId")  UUID departmentId,
                                                                         @PathVariable("userId")        UUID userId) {
        return
                permissionService
                        .getPermissionDescriptionByDepartmentAndUser(departmentId, userId)
                        .map(description -> new ResponseEntity<>(description, HttpStatus.OK))
                        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(PERMISSION_QUERY_END_POINT_V1 + DEPARTMENT_RESOURCE + "/{departmentId}" + USER_RESOURCE + "/{userId}")
    public Mono<ResponseEntity<PermissionDTO>> updatePermissionByDepartmentAndUser(@PathVariable("departmentId")  UUID departmentId,
                                                                                   @PathVariable("userId")        UUID userId,
                                                                                   @RequestBody                   String permissionType) {
        return
                permissionService
                        .updatePermissionByDepartmentAndUser(departmentId, userId, PermissionType.valueOf(permissionType.trim()))
                        .map(permission -> new ResponseEntity<>(permission, HttpStatus.OK))
                        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/hello")
    public Mono<ResponseEntity<String>> hello() {
        return Mono.just(new ResponseEntity<>("OK2", HttpStatus.OK));

    }
}
