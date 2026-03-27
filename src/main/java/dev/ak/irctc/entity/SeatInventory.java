package dev.ak.irctc.entity;

import dev.ak.irctc.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_inventory",
        uniqueConstraints = @UniqueConstraint(
        columnNames = {"train_no", "journey_date", "seat_number"}
), indexes = {
        @Index(name = "idx_seat_inv_search", columnList = "train_no, journey_date, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_no", referencedColumnName = "train_no")
    private Train train;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "coach")
    private String coach;

    @Column(name = "source_station")
    private String sourceStation;

    @Column(name = "destination_station")
    private String destinationStation;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

}
