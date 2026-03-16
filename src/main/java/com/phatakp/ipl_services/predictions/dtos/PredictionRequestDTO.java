package com.phatakp.ipl_services.predictions.dtos;

import com.phatakp.ipl_services.teams.models.TeamEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequestDTO {

    @NotNull(message = "Team is required")
    private TeamEnum team;

    @NotNull(message = "Match is required")
    private Integer matchNumber;


    @NotNull(message = "Amount is required")
    private Short amount;

}
