package com.phatakp.ipl_services.predictions.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.users.models.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "predictions")
public class PredictionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "Amount is required")
    private Short amount;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private PredictionStatus status;


    @NotNull(message = "Result Amount is required")
    private Float resultAmt;

    @NotNull(message = "Double is required")
    private Boolean isDouble;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team",nullable = true)
    @JsonIgnore
    @ToString.Exclude
    private TeamEntity teamEntity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private UserEntity user;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_number",nullable = true)
    @JsonIgnore
    @ToString.Exclude
    private MatchEntity match;

    @Column(nullable = false)
    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime updatedAt;
}
