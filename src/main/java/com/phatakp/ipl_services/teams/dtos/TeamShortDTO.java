package com.phatakp.ipl_services.teams.dtos;

import com.phatakp.ipl_services.teams.models.TeamEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamShortDTO {
    private TeamEnum shortName;
    private String fullName;

}
