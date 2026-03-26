package dev.ak.irctc.entity;
import dev.ak.irctc.enums.HoldStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "seat_hold")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "train_no")
    private Integer trainNo;

    @Column(name = "journey_date")
    private LocalDate journeyDate;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private HoldStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}