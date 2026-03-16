package com.phatakp.ipl_services.predictions.utils;

import com.phatakp.ipl_services.matches.dtos.PredictedWinnerDTO;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.matches.utils.MatchMapper;
import com.phatakp.ipl_services.predictions.dtos.PredictionDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionRequestDTO;
import com.phatakp.ipl_services.predictions.dtos.WinnerTeamDTO;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.predictions.models.PredictionStatus;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.users.models.UserEntity;
import com.phatakp.ipl_services.users.utils.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class PredictionMapper {
    public static PredictionEntity mapRequestDTOtoEntity(PredictionRequestDTO request) {
        return PredictionEntity.builder()
                .amount(request.getAmount())
                .status(PredictionStatus.PLACED)
                .isDouble(false)
                .resultAmt(0F)
                .build();
    }

    public static PredictionDTO mapEntitytoDTO(PredictionEntity prediction) {
        if (prediction == null) {
            return null;
        }
        return PredictionDTO.builder()
                .amount(prediction.getAmount())
                .status(prediction.getStatus())
                .isDouble(prediction.getIsDouble())
                .resultAmt(prediction.getResultAmt())
                .id(prediction.getId())
                .match(MatchMapper.mapEntitytoShortDTO(prediction.getMatch()))
                .team(prediction.getTeamEntity() == null ? null : prediction.getTeamEntity().getShortName())
                .user(UserMapper.mapUserEntityToShortDTO(prediction.getUser()))
                .updatedAt(prediction.getUpdatedAt())
                .build();
    }

    public static PredictionEntity getIPLWinnerPrediction(TeamEntity teamEntity, UserEntity userEntity) {
        return PredictionEntity.builder()
                .teamEntity(teamEntity)
                .amount((short) 500)
                .match(null)
                .user(userEntity)
                .status(PredictionStatus.PLACED)
                .isDouble(false)
                .resultAmt(0F)
                .build();
    }

    public static PredictionEntity getDefaultPrediction(MatchEntity match, UserEntity userEntity) {
        return PredictionEntity.builder()
                .teamEntity(null)
                .amount(match.getMinStake())
                .match(match)
                .user(userEntity)
                .status(PredictionStatus.DEFAULT)
                .isDouble(false)
                .resultAmt(0F)
                .build();
    }

    public static PredictedWinnerDTO mapToPredictedWinnerDTO(PredictionEntity prediction,
                                                        Float amount,
                                                        TeamEnum team) {
        return PredictedWinnerDTO.builder()
                .userId(prediction.getUser().getClerkId())
                .team(team)
                .resultAmt(amount)
                .build();
    }


}
