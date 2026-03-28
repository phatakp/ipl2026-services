package com.phatakp.ipl_services.matches.utils;

import com.phatakp.ipl_services.matches.dtos.MatchStatDTO;
import com.phatakp.ipl_services.matches.dtos.PredictedWinnerDTO;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.matches.models.MatchType;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.predictions.models.PredictionStatus;
import com.phatakp.ipl_services.predictions.utils.PredictionMapper;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchUtils {
    public static final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static short getMinStake(MatchType matchType) {
        if (matchType.equals(MatchType.LEAGUE)) {
            return ((short) 50);
        } else if (matchType.equals(MatchType.FINAL)) {
            return ((short) 200);
        } else {
            return ((short) 100);
        }
    }

    public static Integer getTotalWonAmount(List<MatchStatDTO> stats, TeamEnum winner) {
        return stats.stream()
                .filter(s -> s.getTeam() != null && s.getTeam().equals(winner.name()))
                .mapToInt(MatchStatDTO::getTotalAmount)
                .reduce(0, Integer::sum);
    }

    public static Integer getTotalLostAmount(List<MatchStatDTO> stats,
                                             TeamEnum winner,
                                             Integer doubleLostAmount) {
        return stats.stream()
                .filter(s -> s.getTeam() == null || !s.getTeam().equals(winner.name()))
                .mapToInt(MatchStatDTO::getTotalAmount)
                .reduce(0, Integer::sum) + doubleLostAmount;
    }

    public static Integer getTotalDefaultAmount(List<MatchStatDTO> stats) {
        return stats.stream()
                .filter(s -> s.getTeam() == null)
                .mapToInt(MatchStatDTO::getTotalAmount)
                .reduce(0, Integer::sum);
    }

    public static Integer getTotalNonDefaultAmount(List<MatchStatDTO> stats) {
        return stats.stream()
                .filter(s -> s.getTeam() != null)
                .mapToInt(MatchStatDTO::getTotalAmount)
                .reduce(0, Integer::sum);
    }

    public static float getWinAmount(PredictionEntity prediction,
                                     Integer totalWon,
                                     Integer totalLost,
                                     Optional<Boolean> matchAbandoned) {
        var abandoned = matchAbandoned.orElse(false);
        var winAmount = ((float) prediction.getAmount() / totalWon * totalLost);
        if (prediction.getIsDouble() && !abandoned) {
            winAmount = winAmount + totalLost;
        }
        return winAmount;

    }

    public static float getLoseAmount(PredictionEntity prediction,
                                      Boolean isDoubleWon) {
        return (float) (prediction.getIsDouble() || isDoubleWon
                ? prediction.getAmount() * -2
                : prediction.getAmount() * -1);

    }

    public static void updatePredictionAsWon(
            PredictionEntity prediction,
            float winAmount) {
        var user = prediction.getUser();
        prediction.setStatus(PredictionStatus.WON);
        prediction.setResultAmt(winAmount);
        user.addBalance(winAmount);
        prediction.setUser(user);

    }

    public static void updatePredictionAsLost(
            PredictionEntity prediction,
            float lostAmount) {
        var user = prediction.getUser();
        prediction.setStatus(PredictionStatus.LOST);
        prediction.setResultAmt(lostAmount);
        user.addBalance(lostAmount);
        prediction.setUser(user);
    }


    public static List<PredictedWinnerDTO> settleNormal(
            MatchEntity match,
            TeamEnum winner,
            Integer totalWon,
            Integer totalLost,
            Boolean isDoubleWon,
            Boolean shouldSave) {
        var matchPredictions = match.getPredictions();
        var result = new ArrayList<PredictedWinnerDTO>();

        for (var prediction : matchPredictions) {
            if (prediction.getTeamEntity() != null
                    && prediction.getTeamEntity().getShortName().equals(winner)) {
                var winAmount = MatchUtils.getWinAmount(prediction, totalWon, totalLost, Optional.empty());
                if (shouldSave) {
                    MatchUtils.updatePredictionAsWon(prediction, winAmount);
                }
                result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, winAmount, winner));
            } else {
                var lostAmount = MatchUtils.getLoseAmount(prediction, isDoubleWon);
                if (shouldSave) {
                    MatchUtils.updatePredictionAsLost(prediction, lostAmount);
                }
                result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, lostAmount, winner));
            }
        }
        return result;
    }

    public static List<PredictedWinnerDTO> settleDefaulters(
            MatchEntity match,
            TeamEnum winner,
            Integer totalWon,
            Integer totalLost,
            Boolean shouldSave
    ) {
        var matchPredictions = match.getPredictions();
        var result = new ArrayList<PredictedWinnerDTO>();

        for (var prediction : matchPredictions) {
            if (prediction.getStatus().equals(PredictionStatus.DEFAULT)) {
                var lostAmount = MatchUtils.getLoseAmount(prediction, false);
                if (shouldSave) {
                    MatchUtils.updatePredictionAsLost(prediction, lostAmount);
                }
                result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, lostAmount, winner));
            } else {
                var winAmount = MatchUtils.getWinAmount(prediction,
                        totalWon,
                        totalLost,
                        Optional.of(true));
                if (shouldSave) {
                    MatchUtils.updatePredictionAsWon(prediction, winAmount);
                }
                result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, winAmount, winner));

            }
        }
        return result;
    }

    public static List<PredictedWinnerDTO> settleNoResult(MatchEntity match,
                                                          TeamEnum winner,
                                                          Boolean shouldSave) {
        var matchPredictions = match.getPredictions();
        var result = new ArrayList<PredictedWinnerDTO>();
        for (var prediction : matchPredictions) {
            if (shouldSave) {
                prediction.setStatus(PredictionStatus.NORESULT);
                prediction.setResultAmt(0F);
            }
            result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, 0F, winner));
        }
        return result;
    }

    public static List<PredictedWinnerDTO> settleCompleted(
            MatchEntity match,
            TeamEnum winner,
            List<MatchStatDTO> stats,
            Boolean isDoubleWon,
            Boolean shouldSave) {
        var doubleLostAmount = 0;
        if (match.getIsDouble()) {
            doubleLostAmount = match.getPredictions()
                    .stream()
                    .filter(PredictionEntity::getIsDouble)
                    .filter(p -> !p.getTeamEntity().getShortName().equals(winner))
                    .mapToInt(PredictionEntity::getAmount).sum();
        }

        var totalWon = MatchUtils.getTotalWonAmount(stats, winner);
        var totalLost = MatchUtils.getTotalLostAmount(stats, winner, doubleLostAmount);
        var totalDefault = MatchUtils.getTotalDefaultAmount(stats);


        // Both winners and losers (including defaulters)
        if (totalWon > 0 && totalLost > 0) {
            return settleNormal(match, winner, totalWon, totalLost, isDoubleWon, shouldSave);
        }
        // All lost but have defaulters
        else if (totalLost > 0 && totalDefault > 0) {
            return settleDefaulters(match,winner, totalLost - totalDefault, totalDefault, shouldSave);
        } else {
            return settleNoResult(match,winner, shouldSave);
        }
    }


    public static void settleAbandoned(
            MatchEntity match,
            TeamEnum winner,
            List<MatchStatDTO> stats) {

        var totalDefault = MatchUtils.getTotalDefaultAmount(stats);
        var totalNonDefault = MatchUtils.getTotalNonDefaultAmount(stats);

        if (totalDefault > 0) {
            settleDefaulters(match,winner, totalNonDefault, totalDefault, true);
        } else {
            settleNoResult(match,winner, true);
        }

    }



    public static List<PredictedWinnerDTO> settleFinal(
            List<PredictionEntity> predictions,
            TeamEnum winner,
            List<MatchStatDTO> stats,
            Boolean shouldSave) {


        var totalWon = MatchUtils.getTotalWonAmount(stats, winner);
        var totalLost = MatchUtils.getTotalLostAmount(stats, winner, 0);


        // Both winners and losers (including defaulters)
        if (totalWon > 0 && totalLost > 0) {
            var result = new ArrayList<PredictedWinnerDTO>();

            for (var prediction : predictions) {
                if (prediction.getTeamEntity() != null
                        && prediction.getTeamEntity().getShortName().equals(winner)) {
                    var winAmount = MatchUtils.getWinAmount(prediction, totalWon, totalLost, Optional.empty());
                    if (shouldSave) {
                        MatchUtils.updatePredictionAsWon(prediction, winAmount);
                    }
                    result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, winAmount, winner));
                } else {
                    var lostAmount = MatchUtils.getLoseAmount(prediction, false);
                    if (shouldSave) {
                        MatchUtils.updatePredictionAsLost(prediction, lostAmount);
                    }
                    result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, lostAmount, winner));
                }
            }
            return result;
        }
         else {
            var result = new ArrayList<PredictedWinnerDTO>();
            for (var prediction : predictions) {
                if (shouldSave) {
                    prediction.setStatus(PredictionStatus.NORESULT);
                    prediction.setResultAmt(0F);
                }
                result.add(PredictionMapper.mapToPredictedWinnerDTO(prediction, 0F, winner));
            }
            return result;
        }
    }

}
