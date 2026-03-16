package com.phatakp.ipl_services.teams.services;

import com.phatakp.ipl_services.config.AppProperties;
import com.phatakp.ipl_services.config.exceptions.APIException;
import com.phatakp.ipl_services.teams.TeamRepository;
import com.phatakp.ipl_services.teams.dtos.TeamDTO;
import com.phatakp.ipl_services.teams.dtos.TeamFormDTO;
import com.phatakp.ipl_services.teams.dtos.TeamRequestDTO;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.teams.utils.TeamMapper;
import com.phatakp.ipl_services.teams.utils.TeamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final AppProperties appProperties;

    /**
     * @return List of TeamDTO
     */
    @Override
    public List<TeamDTO> getTeams() {
        if (appProperties.getDebug()) {
            log.info("getAllTeams()");
        }

        var teams=  teamRepository.getStandings()
                .stream()
                .map(TeamMapper::mapEntityToDTO)
                .toList();

        for(var team:teams){
            team.setForm(teamRepository.getTeamForm(team.getShortName().name()));
        }
        return teams;
    }


    /**
     * @param shortName Name of team
     * @return TeamEntity
     */
    @Override
    public TeamEntity validateTeam(TeamEnum shortName, String message) {
        if (appProperties.getDebug()) {
            log.info("validateTeam()");
        }
        var team = teamRepository.findById(shortName);
        if (team.isEmpty()) {
            throw APIException.notFound(message);
        }
        return team.get();
    }

    /**
     * @param request TeamRequestDTO
     * @return TeamEntity
     */
    @Override
    public TeamEntity updateTeam(TeamRequestDTO request) {
        var team = validateTeam(request.getTeam(), "Update Score");
        team.setTeamStats(request.getForScore(), true);
        team.setTeamStats(request.getAgainstScore(), false);
        team.setTeamStandings(
                request.getIsWinner(),
                request.getIsCompleted());
        team.setNrr(TeamUtils.calculateNrr(
                team.getForRuns(),
                team.getForBalls(),
                team.getAgainstRuns(),
                team.getAgainstBalls()));
        return team;
    }

    /**
     * @param request TeamRequestDTO
     * @return TeamEntity
     */
    @Override
    public TeamEntity reverseTeam(TeamRequestDTO request) {
        var team = validateTeam(request.getTeam(), "Reverse Score");
        team.reverseTeamStats(request.getForScore(), true);
        team.reverseTeamStats(request.getAgainstScore(), false);
        team.reverseTeamStandings(
                request.getIsWinner(),
                request.getIsCompleted());
        team.setNrr(TeamUtils.calculateNrr(
                team.getForRuns(),
                team.getForBalls(),
                team.getAgainstRuns(),
                team.getAgainstBalls()));
        return team;
    }

    /**
     * @return list of team form
     */
    @Override
    public List<TeamFormDTO> getTeamForms(TeamEnum team) {
        return teamRepository.getTeamForm(team.name());
    }


}
