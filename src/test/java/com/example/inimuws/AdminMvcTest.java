package com.example.inimuws;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminLoginAuthenticatesInitialUser() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                        .user("email", "admin@example.com")
                        .password("password"))
                .andExpect(authenticated().withUsername("admin@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPagesRenderForAuthenticatedUser() throws Exception {
        assertOk("/admin");
        assertOk("/admin/reservations");
        assertOk("/admin/schedules");
        assertOk("/admin/sales");
        assertOk("/admin/inquiries");
    }

    void assertOk(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk());
    }
}
