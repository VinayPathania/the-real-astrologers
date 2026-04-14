package com.astro.theRealAstrologers.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "birth_profiles")
@Data
public class BirthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personName;
    private LocalDate birthDate;
    private LocalTime birthTime;


    private Double latitude;
    private Double longitude;
    private String cityName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}