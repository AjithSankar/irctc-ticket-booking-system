package dev.ak.irctc.repository;

import dev.ak.irctc.entity.SeatHold;
import dev.ak.irctc.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, UUID> {
}
