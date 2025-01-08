package com.nado.smartcare.employee.controller;

import com.nado.smartcare.employee.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Employee Controller 테스트")
@WebMvcTest(value = EmployeeController.class)
class EmployeeControllerTest {

    private final MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;



    public EmployeeControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[View] [GET] 직원 등록 페이지 호출")
    @Test
    void givenNothing_whenRequestJoinView_thenReturnsJoinView() throws Exception {
        mvc.perform(get("/employee/join"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("employee/join"));
    }
}