package com.phatakp.ipl_services.predictions.dtos;

import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.users.dtos.UserShortDTO;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinnerTeamDTO {
    private UserShortDTO user;
    private TeamEnum team;
    private Float resultAmt;
}
