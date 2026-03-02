package com.habittracker.habit_tracker.controller;

import com.habittracker.habit_tracker.dto.response.AdminStatsResponse;
import com.habittracker.habit_tracker.dto.response.UserResponse;
import com.habittracker.habit_tracker.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllUsers_AsAdmin_Success() throws Exception {
        UserResponse user1 = new UserResponse(1L, "user1", "ROLE_USER");
        UserResponse user2 = new UserResponse(2L, "admin", "ROLE_ADMIN");
        List<UserResponse> users = Arrays.asList(user1, user2);

        when(adminService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].role").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetAllUsers_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetStats_Success() throws Exception {
        AdminStatsResponse stats = new AdminStatsResponse(10, 50);
        when(adminService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.totalHabits").value(50));
    }
}