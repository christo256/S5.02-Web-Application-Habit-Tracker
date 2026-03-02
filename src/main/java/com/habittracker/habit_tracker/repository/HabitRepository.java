package com.habittracker.habit_tracker.repository;

import com.habittracker.habit_tracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Buscar todos los hábitos de un usuario
    List<Habit> findByUserId(Long userId);

    List<Habit> findByUserUsername(String username);
}