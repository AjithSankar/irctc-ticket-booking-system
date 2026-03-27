package dev.ak.irctc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "train_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_no", referencedColumnName = "train_no", nullable = false)
    private Train train;

    @Column(name = "station_code", nullable = false)
    private String stationCode;

    @Column(name = "station_name")
    private String stationName;

    // CRITICAL: Tells us the order of stations (1=Source, 2=First Stop, etc.)
    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    // Uses LocalTime instead of LocalDateTime because schedules repeat daily
    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    // CRITICAL: A train starting on Monday might reach a station on Tuesday.
    // Day 1 = 1, Day 2 = 2, etc.
    @Column(name = "day_count", nullable = false)
    private Integer dayCount;

    @Column(name = "distance_from_source")
    private Integer distanceFromSource;
}