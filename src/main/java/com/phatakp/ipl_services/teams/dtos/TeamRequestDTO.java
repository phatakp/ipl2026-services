package com.phatakp.ipl_services.teams.dtos;

import com.phatakp.ipl_services.teams.models.TeamEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequestDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private TeamEnum team;

    @NotNull
    private Boolean isCompleted;

    @NotNull
    private Boolean isWinner;

    @NotNull
    private TeamScoreDTO forScore;

    @NotNull
    private TeamScoreDTO againstScore;
}
