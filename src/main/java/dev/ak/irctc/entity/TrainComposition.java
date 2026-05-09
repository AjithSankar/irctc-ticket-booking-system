package dev.ak.irctc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "train_composition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainComposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_no", referencedColumnName = "train_no", nullable = false)
    @JsonIgnore
    private Train train;

    @Column(name = "class_type", nullable = false)
    private String classType; // e.g., "SL", "3A", "2A"

    @Column(name = "coach_prefix", nullable = false)
    private String coachPrefix; // e.g., "S", "B", "A"

    @Column(name = "number_of_coaches", nullable = false)
    private Integer numberOfCoaches; // e.g., 10 sleeper coaches

    @Column(name = "seats_per_coach", nullable = false)
    private Integer seatsPerCoach; // e.g., 72 seats per sleeper
}