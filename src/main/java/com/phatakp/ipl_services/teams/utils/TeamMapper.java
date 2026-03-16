package com.phatakp.ipl_services.teams.utils;

import com.phatakp.ipl_services.teams.dtos.TeamDTO;
import com.phatakp.ipl_services.teams.dtos.TeamShortDTO;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    public static TeamDTO mapEntityToDTO(TeamEntity teamEntity) {
        if (teamEntity == null) {return null;}
        return TeamDTO.builder()
                .shortName(teamEntity.getShortName())
                .fullName(teamEntity.getFullName())
                .played(teamEntity.getPlayed())
                .points(teamEntity.getPoints())
                .lost(teamEntity.getLost())
                .won(teamEntity.getWon())
                .draw(teamEntity.getDraw())
                .nrr(teamEntity.getNrr())
                .forBalls(teamEntity.getForBalls())
                .againstBalls(teamEntity.getAgainstBalls())
                .forRuns(teamEntity.getForRuns())
                .againstRuns(teamEntity.getAgainstRuns())
                .build();
    }

    public static TeamShortDTO mapEntityToShortDTO(TeamEntity teamEntity) {
        if (teamEntity == null) {return null;}
        return TeamShortDTO.builder()
                .shortName(teamEntity.getShortName())
                .fullName(teamEntity.getFullName())
                .build();
    }
}
