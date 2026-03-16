package com.phatakp.ipl_services.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    private String clerkId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

        @Column(nullable = false)
    private Float balance;

    @Column(nullable = false)
    private Short doublesLeft;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team",nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private TeamEntity teamEntity;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<PredictionEntity> predictions = new ArrayList<>();

    public UserEntity addBalance(Float amount) {
        this.balance = this.balance+amount;
        return this;
    }

    public UserEntity subtractBalance(Float amount) {
        this.balance = this.balance-amount;
        return this;
    }
}
