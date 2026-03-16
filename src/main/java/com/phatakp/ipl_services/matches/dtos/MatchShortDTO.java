package com.phatakp.ipl_services.matches.dtos;

import com.phatakp.ipl_services.matches.models.MatchStatus;
import com.phatakp.ipl_services.matches.models.MatchType;
import com.phatakp.ipl_services.teams.dtos.TeamShortDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchShortDTO {
    private Integer number;
    private TeamShortDTO homeTeam;
    private TeamShortDTO awayTeam;
    private TeamShortDTO winner;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;
    private MatchType type;
    private boolean hasEntryCutoffPassed;
    private boolean hasStarted;
    private boolean hasDoubleCutoffPassed;
}
