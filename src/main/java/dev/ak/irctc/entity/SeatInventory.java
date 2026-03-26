package dev.ak.irctc.entity;

import dev.ak.irctc.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "seat_inventory", indexes = {
    // Crucial index for the `getAvailability` flow
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

    // Mapping FK to the unique train_no rather than Train PK as defined in your schema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_no", referencedColumnName = "train_no")
    private Train train;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "source_station")
    private String sourceStation;

    @Column(name = "destination_station")
    private String destinationStation;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Version
    private Long version; 
}
