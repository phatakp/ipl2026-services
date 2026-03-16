package com.phatakp.ipl_services.teams.services;

import com.phatakp.ipl_services.teams.dtos.TeamDTO;
import com.phatakp.ipl_services.teams.dtos.TeamFormDTO;
import com.phatakp.ipl_services.teams.dtos.TeamRequestDTO;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.teams.models.TeamEnum;

import java.util.List;

public interface TeamService {
    List<TeamDTO> getTeams();
    TeamEntity validateTeam(TeamEnum shortName, String message);
    TeamEntity updateTeam(TeamRequestDTO request);
    TeamEntity reverseTeam(TeamRequestDTO request);
    List<TeamFormDTO>  getTeamForms(TeamEnum team);
}
