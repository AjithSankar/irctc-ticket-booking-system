package dev.ak.irctc.repository;

import dev.ak.irctc.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    @Query(value = """
            SELECT * FROM seat_inventory
            WHERE train_no = :trainNo
              AND journey_date = :journeyDate
              AND status = 'AVAILABLE'
            ORDER BY seat_number ASC
            LIMIT :numberOfSeats
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<SeatInventory> findAvailableSeatsAndLock(
            @Param("trainNo") Integer trainNo,
            @Param("journeyDate") LocalDate localDate,
            @Param("numberOfSeats") int numberOfSeats
    );

    @Query(value = """
                UPDATE seat_inventory
                SET status = 'BLOCKED',
                    blocked_at = now()
                WHERE id = (
                    SELECT id FROM seat_inventory
                    WHERE train_no = :trainNo
                      AND journey_date = :journeyDate
                      AND status = 'AVAILABLE'
                    LIMIT 1
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING *
            """, nativeQuery = true)
    SeatInventory findAndLockNextAvailableSeat(
            Integer trainNo,
            LocalDate journeyDate
    );

    @Query(value = """
            SELECT COUNT(*) FROM seat_inventory
            WHERE train_no = :trainNo
              AND journey_date = :journeyDate
              AND status = 'AVAILABLE'
              AND coach LIKE :coachPrefix || '%'
            """, nativeQuery = true)
    long countAvailableSeats(
            @Param("trainNo") Integer trainNo,
            @Param("journeyDate") LocalDate journeyDate,
            @Param("coachPrefix") String coachPrefix
    );

    @Query(value = """
            SELECT * FROM seat_inventory
                        WHERE train_no = :trainNo
                          AND journey_date = :journeyDate
                          AND coach = :coach
                          AND seat_number = :seatNumber
            """, nativeQuery = true)
    Optional<SeatInventory> findByTrainAndJourneyDateAndCoachAndSeatNumber(
            Integer trainNo, LocalDate journeyDate, String coach, Integer seatNumber
    );

    @Query("SELECT COUNT(s) > 0 FROM SeatInventory s WHERE s.train.trainNo = :trainNo AND s.journeyDate = :journeyDate")
    boolean existsByTrainNoAndJourneyDate(@Param("trainNo") Integer trainNo, @Param("journeyDate") LocalDate journeyDate);

}
