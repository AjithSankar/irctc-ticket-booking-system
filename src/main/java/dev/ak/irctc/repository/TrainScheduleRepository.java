package dev.ak.irctc.repository;

import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
}
