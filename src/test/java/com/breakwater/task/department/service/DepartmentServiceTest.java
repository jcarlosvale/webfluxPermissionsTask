package com.breakwater.task.department.service;

import com.breakwater.task.department.dto.DepartmentDTO;
import com.breakwater.task.department.model.Department;
import com.breakwater.task.department.repository.DepartmentRepository;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

public class DepartmentServiceTest {

    private static final UUID PARENT_ID  = UUID.randomUUID();
    private static final UUID CHILD_ID   = UUID.randomUUID();
    private static final UUID CHILD_ID_2 = UUID.randomUUID();

    @Tested
    private DepartmentService subject;

    @Injectable
    private DepartmentRepository departmentRepository;

    @Test
    public void testReadDepartmentWithParents() {
        var expectedParentDepartment = new DepartmentDTO(PARENT_ID, PARENT_ID.toString(), null, List.of());
        var expectedChildDepartment = new DepartmentDTO(CHILD_ID, CHILD_ID.toString(), expectedParentDepartment, List.of());
        new Expectations() {{
            departmentRepository.findById(CHILD_ID);
            result = Mono.just(new Department(CHILD_ID, CHILD_ID.toString(), PARENT_ID));

            departmentRepository.findById(PARENT_ID);
            result = Mono.just(new Department(PARENT_ID, PARENT_ID.toString(), null));
        }};

        StepVerifier.create(subject.readDepartmentWithParents(CHILD_ID))
                    .expectNext(expectedChildDepartment)
                    .verifyComplete();
    }

    @Test
    public void testReadDepartmentWithChildren() {
        var expectedChildDepartment1 = new DepartmentDTO(CHILD_ID, CHILD_ID.toString(), null, List.of());
        var expectedChildDepartment2 = new DepartmentDTO(CHILD_ID_2, CHILD_ID_2.toString(), null, List.of());
        var expectedParentDepartment = new DepartmentDTO(PARENT_ID, PARENT_ID.toString(), null, List.of(expectedChildDepartment1, expectedChildDepartment2));
        new Expectations() {{
            departmentRepository.findById(PARENT_ID);
            result = Mono.just(new Department(PARENT_ID, PARENT_ID.toString(), null));

            departmentRepository.findByParentId(PARENT_ID);
            result = Flux.just(new Department(CHILD_ID, CHILD_ID.toString(), PARENT_ID), new Department(CHILD_ID_2, CHILD_ID_2.toString(), PARENT_ID));

            departmentRepository.findByParentId(CHILD_ID);
            result = Flux.empty();

            departmentRepository.findByParentId(CHILD_ID_2);
            result = Flux.empty();
        }};

        StepVerifier.create(subject.readDepartmentWithChildren(PARENT_ID))
                    .expectNext(expectedParentDepartment)
                    .verifyComplete();
    }

}