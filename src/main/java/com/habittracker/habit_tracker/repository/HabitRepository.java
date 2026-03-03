package com.habittracker.habit_tracker.repository;

import com.habittracker.habit_tracker.model.Habit;
import com.habittracker.habit_tracker.model.enums.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {


    List<Habit> findByUserId(Long userId);

    List<Habit> findByUserUsername(String username);
    // Obtener top hábitos por frecuencia ordenados por mejor racha
    @Query("SELECT h FROM Habit h WHERE h.frequency = :frequency ORDER BY h.longestStreak DESC")
    List<Habit> findTopByFrequency(@Param("frequency") Frequency frequency, Pageable pageable);

    // Contar hábitos por frecuencia
    long countByFrequency(Frequency frequency);
}