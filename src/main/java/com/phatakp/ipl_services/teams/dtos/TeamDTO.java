package com.phatakp.ipl_services.teams.dtos;

import com.phatakp.ipl_services.matches.dtos.MatchShortDTO;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDTO {
    @Enumerated(EnumType.STRING)
    private TeamEnum shortName;

    private String fullName;
    private Short played;
    private Short won;
    private Short lost;
    private Short draw;
    private Short points;
    private float nrr;
    private Integer forRuns;
    private Integer forBalls;
    private Integer againstRuns;
    private Integer againstBalls;
    private List<TeamFormDTO> form = new ArrayList<>();
}
