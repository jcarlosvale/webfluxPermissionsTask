package com.breakwater.task.permission.controller;

import com.breakwater.task.department.model.Department;
import com.breakwater.task.permission.dto.PermissionDTO;
import com.breakwater.task.permission.enums.PermissionType;
import com.breakwater.task.permission.model.Permission;
import com.breakwater.task.permission.repository.PermissionRepository;
import com.breakwater.task.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.breakwater.task.config.DataSeeder.*;
import static com.breakwater.task.permission.controller.PermissionController.PERMISSION_END_POINT_V1;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient(timeout = "36000")
class PermissionControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PermissionRepository permissionRepository;

    @BeforeEach
    public void clean() {
        permissionRepository.deleteAll().subscribe();
    }

    @Test
    public void getEmptyPermissionFromRootTest() {
        webTestClient.get().uri(PERMISSION_END_POINT_V1, COMPANY_ID, JOHN_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(PermissionType.NONE.name());
    }

    @Test
    public void getEmptyPermissionFromLeafTest() {
        webTestClient.get().uri(PERMISSION_END_POINT_V1, RECRUITING_ID, JOHN_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(PermissionType.NONE.name());
    }

    @Test
    public void getPermissionFromInexistentEmployeeTest() {
        String FAKE_EMPLOYEE = "87dc745a-5d2f-4482-bb8e-a0eed2bc3fe0";
        webTestClient.get().uri(PERMISSION_END_POINT_V1, FINANCE_ID, FAKE_EMPLOYEE)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("User not found ID: " + FAKE_EMPLOYEE);
    }

    @Test
    public void getPermissionFromInexistentDepartmentTest() {
        String FAKE_DEPARTMENT = "87dc745a-5d2f-4482-bb8e-a0eed2bc3fe0";
        webTestClient.get().uri(PERMISSION_END_POINT_V1, FAKE_DEPARTMENT, JOHN_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Department not found ID: " + FAKE_DEPARTMENT);
    }

    @Test
    public void getPermissionByInheritanceTest() {
        createPermission(JOHN_ID, COMPANY_ID, PermissionType.VIEW);
        webTestClient.get().uri(PERMISSION_END_POINT_V1, RECRUITING_ID, JOHN_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(PermissionType.VIEW.name());
    }

    @Test
    public void getPermissionTest() {
        createPermission(JOHN_ID, COMPANY_ID, PermissionType.VIEW);
        createPermission(JOHN_ID, FINANCE_ID, PermissionType.EDIT);
        webTestClient.get().uri(PERMISSION_END_POINT_V1, FINANCE_ID, JOHN_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(PermissionType.EDIT.name());
    }

    @Test
    public void setPermissionTest() {
        webTestClient.put().uri(PERMISSION_END_POINT_V1, COMPANY_ID, JOHN_ID)
                .body(Mono.just(PermissionType.EDIT.name()), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PermissionDTO.class)
                .consumeWith(permissionDTOEntityExchangeResult -> {
                    PermissionDTO permissionDTO = permissionDTOEntityExchangeResult.getResponseBody();
                    assertEquals(JOHN_ID, permissionDTO.getUserDTO().getId());
                    assertEquals(COMPANY_ID, permissionDTO.getDepartmentDTO().getId());
                    assertEquals(PermissionType.EDIT, permissionDTO.getType());
                });
        Permission actualPermission = permissionRepository.findByDepartment_IdAndUser_Id(COMPANY_ID, JOHN_ID).block();
        assertEquals(PermissionType.EDIT, actualPermission.getType());
    }

    @Test
    public void setPermissionUsingInexistentEmployeeTest() {
        String FAKE_EMPLOYEE = "87dc745a-5d2f-4482-bb8e-a0eed2bc3fe0";
        webTestClient.put().uri(PERMISSION_END_POINT_V1, FINANCE_ID, FAKE_EMPLOYEE)
                .body(Mono.just(PermissionType.EDIT.name()), String.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("User not found ID: " + FAKE_EMPLOYEE);
    }

    @Test
    public void setPermissionUsingInexistentDepartmentTest() {
        String FAKE_DEPARTMENT = "87dc745a-5d2f-4482-bb8e-a0eed2bc3fe0";
        webTestClient.put().uri(PERMISSION_END_POINT_V1, FAKE_DEPARTMENT, JOHN_ID)
                .body(Mono.just(PermissionType.VIEW.name()), String.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Department not found ID: " + FAKE_DEPARTMENT);
    }

    private void createPermission(UUID userID, UUID deptID, PermissionType permissionType) {
        User user = new User(userID, null);
        Department department = new Department(deptID, null, null);
        Permission permission = Permission.builder().user(user).department(department).type(permissionType).build();
        permissionRepository.insert(permission).subscribe();
    }
}
