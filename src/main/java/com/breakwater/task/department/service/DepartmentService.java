package com.breakwater.task.department.service;

import com.breakwater.task.department.dto.DepartmentDTO;
import com.breakwater.task.department.model.Department;
import com.breakwater.task.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Mono<DepartmentDTO> readDepartmentWithParents(UUID departmentId) {
        return departmentRepository.findById(departmentId)
                                   .flatMap(department -> addParent(department.getParentId(), department));
    }

    public Mono<DepartmentDTO> readDepartmentWithChildren(UUID departmentId) {
        return departmentRepository.findById(departmentId)
                                   .flatMap(this::addChildren);
    }

    private Mono<DepartmentDTO> addParent(UUID parentId, Department department) {
        return Mono.justOrEmpty(parentId)
                   .flatMap(departmentRepository::findById)
                   .flatMap(parentDepartment -> addParent(parentDepartment.getParentId(), parentDepartment))
                   .map(parentDepartmentDTO -> new DepartmentDTO(department.getId(), department.getName(), parentDepartmentDTO, List.of()))
                   .switchIfEmpty(Mono.just(new DepartmentDTO(department.getId(), department.getName(), null, List.of())));
    }

    private Mono<DepartmentDTO> addChildren(Department department) {
        return departmentRepository.findByParentId(department.getId())
                                   .flatMap(this::addChildren)
                                   .collectList()
                                   .map(childDepartments -> new DepartmentDTO(department.getId(), department.getName(), null, childDepartments));
    }

}
