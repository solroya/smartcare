package com.nado.smartcare.member.repository;

import com.nado.smartcare.member.controller.MemberController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Member 컨트롤러")
@WebMvcTest(MemberController.class)
class MemberRepositoryTest {

    private final MockMvc mvc;

    public MemberRepositoryTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[View] [GET] 멤버 리스트 페이지 - 정상호출")
    @Test
    public void givenNothing_whenRequestMemberView_thenReturnsMembersView() throws Exception {
        // Given

        // When

        // Then
        mvc.perform(MockMvcRequestBuilders.get("/member"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.model().attributeExists("members"));
    }

}