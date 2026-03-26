package dev.ak.irctc.entity;

import dev.ak.irctc.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id", updatable = false, nullable = false)
    private UUID bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_no", referencedColumnName = "train_no", nullable = false)
    private Train train;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passenger> passengers;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;
}
