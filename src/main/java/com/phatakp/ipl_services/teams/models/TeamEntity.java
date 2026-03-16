package com.phatakp.ipl_services.teams.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.teams.dtos.TeamScoreDTO;
import com.phatakp.ipl_services.users.models.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teams")
public class TeamEntity {

    @Id
    @Enumerated(EnumType.STRING)
    private TeamEnum shortName;

    @NotNull
    private String fullName;

    @NotNull
    private Short played;

    @NotNull
    private Short won;

    @NotNull
    private Short lost;

    @NotNull
    private Short draw;

    @NotNull
    private Short points;

    @NotNull
    private float nrr;

    @NotNull
    private Integer forRuns;

    @NotNull
    private Integer forBalls;

    @NotNull
    private Integer againstRuns;

    @NotNull
    private Integer againstBalls;

    @OneToMany(mappedBy = "teamEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "homeTeamEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<MatchEntity> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "awayTeamEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<MatchEntity> awayMatches = new ArrayList<>();

    @OneToMany(mappedBy = "winnerEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<MatchEntity> winnerMatches = new ArrayList<>();

    @OneToMany(mappedBy = "teamEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<PredictionEntity> predictions = new ArrayList<>();

    public void setTeamStandings(Boolean isWinner, Boolean isCompleted) {
        this.played++;
        if (isWinner && isCompleted) {
            this.won++;
            this.points = (short) (this.points+2);
        }
        else if (!isWinner && isCompleted) {
            this.lost++;
        } else {
            this.points++;
            this.draw++;
        }
    }

    public void reverseTeamStandings(Boolean isWinner, Boolean isCompleted) {
        this.played--;
        if (isWinner && isCompleted) {
            this.won--;
            this.points = (short) (this.points-2);
        }
        else if (!isWinner && isCompleted) {
            this.lost--;
        } else {
            this.points--;
            this.draw--;
        }
    }


    public void setTeamStats(TeamScoreDTO scoreDTO, Boolean forStats) {
        if (scoreDTO == null) { return; }
        if (forStats) {
            this.forRuns += scoreDTO.getRuns();
            this.forBalls += scoreDTO.getBalls();
        } else {
            this.againstRuns += scoreDTO.getRuns();
            this.againstBalls += scoreDTO.getBalls();
        }
    }

    public void reverseTeamStats(TeamScoreDTO scoreDTO,Boolean forStats) {
        if (scoreDTO == null) { return; }
        if (forStats) {
            this.forRuns -= scoreDTO.getRuns();
            this.forBalls -= scoreDTO.getBalls();
        } else {
            this.againstRuns -= scoreDTO.getRuns();
            this.againstBalls -= scoreDTO.getBalls();
        }
    }



}
