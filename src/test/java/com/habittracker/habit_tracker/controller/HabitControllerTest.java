package com.habittracker.habit_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habittracker.habit_tracker.dto.request.HabitRequest;
import com.habittracker.habit_tracker.dto.response.HabitResponse;
import com.habittracker.habit_tracker.model.enums.Frequency;
import com.habittracker.habit_tracker.service.HabitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HabitService habitService;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetAllHabits_Success() throws Exception {
        HabitResponse habit1 = new HabitResponse(1L, "Read", "Read 30 min", Frequency.DAILY, 0, 0, null, 1L);
        HabitResponse habit2 = new HabitResponse(2L, "Exercise", "Gym", Frequency.WEEKLY, 0, 0, null, 1L);
        List<HabitResponse> habits = Arrays.asList(habit1, habit2);

        when(habitService.getAllUserHabits(anyString())).thenReturn(habits);

        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Read"))
                .andExpect(jsonPath("$[1].name").value("Exercise"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCreateHabit_Success() throws Exception {
        HabitRequest request = new HabitRequest("Meditate", "10 minutes daily", Frequency.DAILY);
        HabitResponse response = new HabitResponse(1L, "Meditate", "10 minutes daily", Frequency.DAILY, 0, 0, null, 1L);

        when(habitService.createHabit(any(HabitRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Meditate"))
                .andExpect(jsonPath("$.frequency").value("DAILY"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCompleteHabit_Success() throws Exception {
        HabitResponse response = new HabitResponse(
                1L, "Read", "Read 30 min", Frequency.DAILY,
                1, 1, LocalDate.now(), 1L
        );

        when(habitService.completeHabit(anyLong(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/habits/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak").value(1))
                .andExpect(jsonPath("$.longestStreak").value(1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDeleteHabit_Success() throws Exception {
        mockMvc.perform(delete("/api/habits/1"))
                .andExpect(status().isNoContent());
    }
}