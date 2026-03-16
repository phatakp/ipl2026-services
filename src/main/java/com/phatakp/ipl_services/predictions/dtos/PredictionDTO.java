package com.phatakp.ipl_services.predictions.dtos;

import com.phatakp.ipl_services.matches.dtos.MatchShortDTO;
import com.phatakp.ipl_services.predictions.models.PredictionStatus;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.users.dtos.UserShortDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDTO {
    private String id;
    private TeamEnum team;
    private UserShortDTO user;
    private MatchShortDTO match;

    @Enumerated(EnumType.STRING)
    private PredictionStatus status;

    private Float resultAmt;
    private Short amount;
    private Boolean isDouble;
    private LocalDateTime updatedAt;
}
