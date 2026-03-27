package dev.ak.irctc.entity;

import dev.ak.irctc.enums.TrainType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "source_station")
    private String sourceStation;

    @Column(name = "destination_station")
    private String destinationStation;

    @Column(name = "runs_on")
    private String runsOn;

    // One Train has Many Schedule Stops.
    // OrderBy ensures that when we fetch the schedule, it's strictly ordered from source to destination.
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("stopSequence ASC")
    private List<TrainSchedule> routeSchedules = new ArrayList<>();
}