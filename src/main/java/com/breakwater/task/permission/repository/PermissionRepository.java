package com.breakwater.task.permission.repository;

import com.breakwater.task.permission.model.Permission;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PermissionRepository extends ReactiveMongoRepository<Permission, UUID> {
    Mono<Permission> findByDepartment_IdAndUser_Id(UUID departmentId, UUID userId);
}
