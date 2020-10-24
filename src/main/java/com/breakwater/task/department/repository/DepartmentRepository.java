package com.breakwater.task.department.repository;

import com.breakwater.task.department.model.Department;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface DepartmentRepository extends ReactiveMongoRepository<Department, UUID> {

    Flux<Department> findByParentId(UUID parentId);

}
