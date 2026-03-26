package dev.ak.irctc.entity;

import dev.ak.irctc.enums.TrainType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_no", unique = true, nullable = false)
    private Integer trainNo;

    @Column(name = "train_name")
    private String trainName;

    @Enumerated(EnumType.STRING)
    @Column(name = "train_type")
    private TrainType trainType;

    @Column(name = "from_station")
    private String fromStation;

    @Column(name = "destination_station")
    private String destinationStation;

    @Column(name = "runs_on")
    private String runsOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private TrainSchedule schedule;
}