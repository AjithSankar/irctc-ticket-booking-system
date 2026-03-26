package dev.ak.irctc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    private Integer age;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    private String gender;
    private String country;
    
    @Column(unique = true, nullable = false)
    private String email;
}