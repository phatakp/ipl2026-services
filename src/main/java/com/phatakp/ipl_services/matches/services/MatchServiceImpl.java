package com.phatakp.ipl_services.matches.services;

import com.phatakp.ipl_services.config.AppProperties;
import com.phatakp.ipl_services.config.exceptions.APIException;
import com.phatakp.ipl_services.matches.MatchRepository;
import com.phatakp.ipl_services.matches.dtos.MatchDTO;
import com.phatakp.ipl_services.matches.dtos.MatchRequestDTO;
import com.phatakp.ipl_services.matches.dtos.PredictedWinnerDTO;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.matches.utils.MatchMapper;
import com.phatakp.ipl_services.matches.utils.MatchUtils;
import com.phatakp.ipl_services.matches.utils.MatchValidations;
import com.phatakp.ipl_services.predictions.PredictionRepository;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.predictions.utils.PredictionMapper;
import com.phatakp.ipl_services.predictions.models.PredictionStatus;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.teams.services.TeamService;
import com.phatakp.ipl_services.users.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final TeamService teamService;
    private final UserService userService;
    private final AppProperties appProperties;
    private final MatchValidations matchValidations;
    private final PredictionRepository predictionRepository;

    private boolean getIsDoubleWon(MatchEntity match, TeamEnum winner) {
        if (match.getIsDouble()) {
            return matchRepository.isDoubleWon(match.getNumber(), winner.name());
        }
        return false;
    }

    private void updateTeams(MatchEntity match) {
        var homeTeamRequestDTO = MatchMapper.mapEntitytoTeamRequestDTO(match, true);
        var awayTeamRequestDTO = MatchMapper.mapEntitytoTeamRequestDTO(match, false);
        var homeTeam = teamService.updateTeam(homeTeamRequestDTO);
        var awayTeam = teamService.updateTeam(awayTeamRequestDTO);
        match.setHomeTeamEntity(homeTeam);
        match.setAwayTeamEntity(awayTeam);
        match.setIsUpdated(true);
    }

    private void reverseTeams(MatchEntity match) {
        var homeTeamRequestDTO = MatchMapper.mapEntitytoTeamRequestDTO(match, true);
        var awayTeamRequestDTO = MatchMapper.mapEntitytoTeamRequestDTO(match, false);
        var homeTeam = teamService.reverseTeam(homeTeamRequestDTO);
        var awayTeam = teamService.reverseTeam(awayTeamRequestDTO);
        match.setHomeTeamEntity(homeTeam);
        match.setAwayTeamEntity(awayTeam);
        match.setIsUpdated(true);
    }

    /**
     * @return list of MatchDTO
     */
    @Override
    public List<MatchDTO> getFixtures() {
        if (appProperties.getDebug()) {
            log.info("getAllFixtures()");
        }
        return matchRepository.getFixtures()
                .stream().limit(7)
                .map(MatchMapper::mapEntitytoDTO)
                .toList();
    }

    /**
     * @return list of MatchDTO
     */
    @Override
    public List<MatchDTO> getResults() {
        if (appProperties.getDebug()) {
            log.info("getAllResults()");
        }
        return matchRepository.getResults()
                .stream()
                .map(MatchMapper::mapEntitytoDTO)
                .toList();
    }

    /**
     * @param matchNum number for the match
     * @return MatchDTO
     */
    @Override
    @Transactional
    public MatchDTO getMatchByNum(Integer matchNum) {
        if (appProperties.getDebug()) {
            log.info("getMatchByNum()");
        }
        return matchRepository.getMatchByNumber(matchNum)
                .map(MatchMapper::mapEntitytoDTO)
                .orElse(null);
    }

    /**
     * @param matchNum number for the match
     * @return MatchEntity
     */
    @Override
    public MatchEntity validateMatchByNum(Integer matchNum) {
        return matchValidations.validateByNumber(matchNum);
    }

    /**
     * @param request Details for the match
     * @return MatchDTO
     */
    @Override
    public MatchDTO addNewMatch(MatchRequestDTO request) {
        if (appProperties.getDebug()) {
            log.info("addNewMatch()");
        }
        var team1 = teamService.validateTeam(request.getHomeTeam(), "Home Team not found");
        var team2 = teamService.validateTeam(request.getAwayTeam(), "Away Team not found");

        request.setMinStake(MatchUtils.getMinStake(request.getType()));
        var match = MatchMapper.mapDTOtoEntity(request);
        match.setHomeTeamEntity(team1);
        match.setAwayTeamEntity(team2);
        match.setWinnerEntity(null);

        var savedMatch = matchRepository.save(match);
        return MatchMapper.mapEntitytoDTO(savedMatch);
    }

    /**
     * @param request Details for the match
     * @return MatchDTO
     */
    @Override
    @Transactional
    public MatchDTO updateMatch(MatchRequestDTO request) {
        if (appProperties.getDebug()) {
            log.info("updateMatch()");
        }
        var match = matchValidations.validateByNumber(request.getNumber());
        MatchValidations.validateDefaultBets(match);
        MatchValidations.validateWinner(request.getStatus(), request.getWinner());

        var winner = request.getWinner() == null
                ? null
                : teamService.validateTeam(request.getWinner(),
                "Winner not found");

        var updatedMatch = MatchMapper.updateDTOtoEntity(request, match);
        updatedMatch.setWinnerEntity(winner);

        var stats = matchRepository.getStats(updatedMatch.getNumber());

        if (updatedMatch.isCompleted()) {
            assert winner != null;
            var isDoubleWon = this.getIsDoubleWon(updatedMatch, winner.getShortName());
            MatchUtils.settleCompleted(match,
                    match.getWinnerEntity().getShortName(),
                    stats,isDoubleWon,true);
            updatedMatch.setIsUpdated(true);

            if (match.isLeague()) {
                this.updateTeams(updatedMatch);
            }
            if (match.isFinal()) {
                var finalPredictions = predictionRepository.getFinalPredictions();
                var finalStats = matchRepository.getFinalStats();
                MatchUtils.settleFinal(finalPredictions,
                        updatedMatch.getWinnerEntity().getShortName(),
                        finalStats, true);
            }
        } else if (updatedMatch.isAbandoned()) {
            MatchUtils.settleAbandoned(match,null,stats);
            if (match.isLeague()) {
                this.updateTeams(updatedMatch);
            }
            updatedMatch.setIsUpdated(true);
        }


        var savedMatch = matchRepository.save(updatedMatch);
        return MatchMapper.mapEntitytoDTO(savedMatch);
    }

    /**
     * @param team Shortname of team
     * @return List of MatchDTO
     */
    @Override
    public List<MatchDTO> getFixturesByTeam(TeamEnum team) {
        if (appProperties.getDebug()) {
            log.info("getAllFixturesByTeam()");
        }
        return matchRepository.getFixturesByTeam(team)
                .stream().limit(5)
                .map(MatchMapper::mapEntitytoDTO)
                .toList();
    }

    /**
     * @param team shortname of team
     * @return list of MatchDTO
     */
    @Override
    public List<MatchDTO> getResultsByTeam(TeamEnum team) {
        if (appProperties.getDebug()) {
            log.info("getAllResultsByTeam()");
        }
        return matchRepository.getResultsByTeam(team)
                .stream().limit(5)
                .map(MatchMapper::mapEntitytoDTO)
                .toList();
    }

    /**
     * @param matchNum number for match
     */
    @Override
    @Transactional
    public void addDefaultPredictionsForMatch(Integer matchNum) {
        if (appProperties.getDebug()) {
            log.info("addDefaultPredictionsForMatch()");
        }
        var match = validateMatchByNum(matchNum);
        var defaultUsers = userService.getDefaultUsersForMatch(matchNum);
        log.info("default users");
        var result = match.getPredictions();
        if (match.hasEntryCutoffPassed()) {
            for (var user : defaultUsers) {
                log.info(user.getClerkId());
                var prediction = PredictionMapper.getDefaultPrediction(match, user);
                result.add(prediction);
            }
            match.setPredictions(result);
            match.setDefaultBetsAdded(true);
            matchRepository.save(match);
        } else {
            throw APIException.invalidData("Cutoff not passed for default");
        }
    }

    /**
     * @param matchNum Number for match
     */
    @Override
    @Transactional
    public void reversePredictionsForMatch(Integer matchNum) {
        if (appProperties.getDebug()) {
            log.info("reversePredictionsForMatch()");
        }
        var match = validateMatchByNum(matchNum);
        MatchValidations.validateIsUpdated(match);

        var matchPredictions = match.getPredictions();

        var result = new ArrayList<PredictionEntity>();

        for (var prediction : matchPredictions) {
            var reverseAmt = prediction.getResultAmt();
            var user = prediction.getUser();
            prediction.setResultAmt(0F);
            if (prediction.getStatus().equals(PredictionStatus.LOST)) {
                user.addBalance(reverseAmt*-1);
                if (prediction.getTeamEntity() == null) {
                    prediction.setStatus(PredictionStatus.DEFAULT);
                } else {
                    prediction.setStatus(PredictionStatus.PLACED);
                }
            } else {
                user.subtractBalance(reverseAmt);
                prediction.setStatus(PredictionStatus.PLACED);
            }
            prediction.setUser(user);
            result.add(prediction);
        }

        if (match.isLeague()) {
            this.reverseTeams(match);
        }

        if (match.isFinal()) {
            var finalPredictions = predictionRepository.getFinalPredictions();
            for (var prediction : finalPredictions) {
                var reverseAmt = prediction.getResultAmt();
                var user = prediction.getUser();
                prediction.setResultAmt(0F);
                prediction.setStatus(PredictionStatus.PLACED);
                if (prediction.getStatus().equals(PredictionStatus.LOST)) {
                    user.addBalance(reverseAmt*-1);
                } else {
                    user.subtractBalance(reverseAmt);
                }
                prediction.setUser(user);
            }
        }

        match.setPredictions(result);
        match.setIsUpdated(false);
        matchRepository.save(match);
    }

    /**
     * @param matchNum number for match
     * @return list of ResultAmountDTO
     */
    @Override
    @Transactional
    public List<PredictedWinnerDTO> getPredictionTeamWinner(Integer matchNum) {
        var match = matchValidations.validateByNumber(matchNum);
        var result = new ArrayList<PredictedWinnerDTO>();
        var usersCount = userService.getUsersCount();
        var shouldReturn = match.getDefaultBetsAdded() || usersCount == match.getPredictions().size();
        if (match.isScheduled() && shouldReturn) {
            var stats = matchRepository.getStats(match.getNumber());
            for (var team: match.getTeams()) {
                var isDoubleWon = this.getIsDoubleWon(match, team.getShortName());
                var res = MatchUtils.settleCompleted(match,
                        team.getShortName(),
                        stats,
                        isDoubleWon,
                        false);
                result.addAll(res);
            }
            return result;


        }
        return List.of();
    }
}