package com.phatakp.ipl_services.matches.services;

import com.phatakp.ipl_services.matches.dtos.MatchDTO;
import com.phatakp.ipl_services.matches.dtos.MatchRequestDTO;
import com.phatakp.ipl_services.matches.dtos.PredictedWinnerDTO;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.teams.models.TeamEnum;

import java.util.List;

public interface MatchService {
    List<MatchDTO> getFixtures();
    List<MatchDTO> getResults();
    MatchDTO getMatchByNum(Integer matchNum);
    MatchEntity validateMatchByNum(Integer matchNum);
    MatchDTO addNewMatch(MatchRequestDTO matchDTO);
    MatchDTO updateMatch(MatchRequestDTO matchDTO);
    List<MatchDTO> getFixturesByTeam(TeamEnum team);
    List<MatchDTO> getResultsByTeam(TeamEnum team);
    void addDefaultPredictionsForMatch(Integer matchNum);
    void reversePredictionsForMatch(Integer matchNum);
    List<PredictedWinnerDTO> getPredictionTeamWinner(Integer matchNum);
}
