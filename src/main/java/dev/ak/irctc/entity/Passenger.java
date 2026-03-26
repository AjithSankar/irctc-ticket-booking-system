package dev.ak.irctc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "passengers")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "age")
    private Integer age;

    @Column(name = "name")
    private String name;

    @Column(name = "coach")
    private String coach;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "berth_type")
    private String berthType;

}
