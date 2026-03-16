package com.phatakp.ipl_services.matches.dtos;

import com.phatakp.ipl_services.matches.models.MatchResultType;
import com.phatakp.ipl_services.matches.models.MatchStatus;
import com.phatakp.ipl_services.matches.models.MatchType;
import com.phatakp.ipl_services.teams.dtos.TeamShortDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private Integer number;
    private TeamShortDTO homeTeam;
    private TeamShortDTO awayTeam;
    private TeamShortDTO winner;
    private LocalDate date;
    private String time;
    private String venue;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Enumerated(EnumType.STRING)
    private MatchType type;

    @Enumerated(EnumType.STRING)
    private MatchResultType resultType;

    private Short resultMargin;
    private Short minStake;
    private Boolean isDouble;
    private String homeScore;
    private String awayScore;
    private String homeOvers;
    private String awayOvers;
    private Short maxDoubleAmt;
    private boolean isUpdated;
    private boolean defaultBetsAdded;
    private boolean hasEntryCutoffPassed;
    private boolean hasStarted;
    private boolean hasDoubleCutoffPassed;
}
