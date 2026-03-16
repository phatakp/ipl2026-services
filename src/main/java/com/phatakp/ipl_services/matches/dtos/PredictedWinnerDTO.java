package com.phatakp.ipl_services.matches.dtos;

import com.phatakp.ipl_services.predictions.dtos.WinnerTeamDTO;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.users.dtos.UserShortDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictedWinnerDTO {
    private String userId;
    private TeamEnum team;
    private Float resultAmt;

}
