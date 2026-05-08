package dev.ak.irctc.repository;

import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    Optional<Booking> findByBookingId(UUID bookingId);

    List<Booking> findAllByUserOrderByCreatedAtDesc(User user);
}
