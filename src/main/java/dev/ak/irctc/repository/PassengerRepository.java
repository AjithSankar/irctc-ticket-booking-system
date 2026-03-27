package dev.ak.irctc.repository;

import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    List<Passenger> findAllByBooking(Booking booking);
}
