package com.habittracker.habit_tracker.dto.mapper;

import com.habittracker.habit_tracker.dto.response.UserResponse;
import com.habittracker.habit_tracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}