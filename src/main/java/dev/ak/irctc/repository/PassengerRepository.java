package dev.ak.irctc.repository;

import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    List<Passenger> findAllByBooking(Booking booking);

    // Finds the oldest WAITING_LIST passenger for a specific train and date
    @Query(value = """
        SELECT p.* FROM passengers p 
        JOIN bookings b ON p.booking_id = b.booking_id
        JOIN trains t ON b.train_no = t.id
        WHERE t.train_no = :trainNo 
          AND b.journey_date = :journeyDate 
          AND p.status = 'WAITING_LIST' 
        ORDER BY b.created_at ASC, p.id ASC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<Passenger> findNextWaitingListPassenger(
            @Param("trainNo") Integer trainNo,
            @Param("journeyDate") LocalDate journeyDate
    );

}
