package dev.ak.irctc.repository;

import dev.ak.irctc.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
