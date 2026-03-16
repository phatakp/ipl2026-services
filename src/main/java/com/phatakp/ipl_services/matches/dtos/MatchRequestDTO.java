package com.phatakp.ipl_services.matches.dtos;

import com.phatakp.ipl_services.matches.models.MatchResultType;
import com.phatakp.ipl_services.matches.models.MatchStatus;
import com.phatakp.ipl_services.matches.models.MatchType;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDTO {
    @NotNull(message = "Number is required")
    private Integer number;

    @NotNull(message = "Home Team is required")
    @Enumerated(EnumType.STRING)
    private TeamEnum homeTeam;

    @NotNull(message = "Away Team is required")
    @Enumerated(EnumType.STRING)
    private TeamEnum awayTeam;

    @Enumerated(EnumType.STRING)
    private TeamEnum winner;

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

    private Short maxDoubleAmt=100;
    private Boolean isUpdated=false;

    private Short resultMargin;
    private Short minStake;
    private Boolean isDouble;
    private String homeScore;
    private String awayScore;
    private String homeOvers;
    private String awayOvers;
}
