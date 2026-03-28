package com.phatakp.ipl_services.matches.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phatakp.ipl_services.matches.utils.MatchUtils;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.utils.TimeUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matches")
public class MatchEntity {
    @Id
    private Integer number;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Time is required")
    private String time;

    @NotNull(message = "Venue is required")
    private String venue;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @NotNull(message = "Match Type is required")
    @Enumerated(EnumType.STRING)
    private MatchType type;

    @Enumerated(EnumType.STRING)
    private MatchResultType resultType;

    private Short resultMargin;

    @NotNull(message = "Min Stake is required")
    private Short minStake;

    @NotNull(message = "Match Double is required")
    private Boolean isDouble;

    @NotNull(message = "Match Default Bets is required")
    private Boolean defaultBetsAdded = false;

    @NotNull(message = "Max Double Amt is required")
    private Short maxDoubleAmt = 50;

    @NotNull(message = "Is Updated is required")
    private Boolean isUpdated = false;

    private String homeScore;
    private String awayScore;
    private String homeOvers;
    private String awayOvers;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "home_team", nullable = false)
    @ToString.Exclude
    private TeamEntity homeTeamEntity;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "away_team", nullable = false)
    @ToString.Exclude
    private TeamEntity awayTeamEntity;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner")
    @ToString.Exclude
    private TeamEntity winnerEntity;

    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<PredictionEntity> predictions = new ArrayList<>();

    public List<TeamEntity> getTeams() {
        List<TeamEntity> teams = new ArrayList<>();
        teams.add(homeTeamEntity);
        teams.add(awayTeamEntity);
        return teams;
    }

    public boolean isLeague() {
        return this.getType().equals(MatchType.LEAGUE);
    }

    public boolean isScheduled() {
        return this.getStatus().equals(MatchStatus.SCHEDULED);
    }

    public boolean isCompleted() {
        return this.getStatus().equals(MatchStatus.COMPLETED);
    }

    public boolean isAbandoned() {
        return this.getStatus().equals(MatchStatus.ABANDONED);
    }


    public boolean hasEntryCutoffPassed() {
        var matchTime = ZonedDateTime.parse(
                this.getDate().format(MatchUtils.customFormatter)
                        + "T" +
                        this.getTime() +
                        "+05:30[Asia/Kolkata]");
        var currentTime = TimeUtil.getCurrentISTTime();
        var entryCutoffTime = matchTime.minusMinutes(30);
        return currentTime.isEqual(entryCutoffTime) || currentTime.isAfter(entryCutoffTime);
    }

    public boolean hasMatchStarted() {
        var matchTime = ZonedDateTime.parse(
                this.getDate().format(MatchUtils.customFormatter)
                        + "T" +
                        this.getTime() +
                        "+05:30[Asia/Kolkata]");
        var currentTime = TimeUtil.getCurrentISTTime();
        return currentTime.isEqual(matchTime) || currentTime.isAfter(matchTime);
    }

    public boolean hasDoubleCutoffPassed() {
        var matchTime = ZonedDateTime.parse(
                this.getDate().format(MatchUtils.customFormatter)
                        + "T" +
                        this.getTime() +
                        "+05:30[Asia/Kolkata]");
        var currentTime = TimeUtil.getCurrentISTTime();
        var doubleCutoffTime = matchTime.plusMinutes(30);
        return currentTime.isEqual(doubleCutoffTime) || currentTime.isAfter(doubleCutoffTime);
    }
}
