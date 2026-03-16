package com.phatakp.ipl_services.matches.utils;

import com.phatakp.ipl_services.matches.dtos.MatchDTO;
import com.phatakp.ipl_services.matches.dtos.MatchRequestDTO;
import com.phatakp.ipl_services.matches.dtos.MatchShortDTO;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.teams.dtos.TeamScoreDTO;
import com.phatakp.ipl_services.teams.utils.TeamMapper;
import com.phatakp.ipl_services.teams.dtos.TeamRequestDTO;
import com.phatakp.ipl_services.teams.utils.TeamUtils;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {
    public static MatchEntity mapDTOtoEntity(MatchRequestDTO dto) {
        return MatchEntity.builder()
                .number(dto.getNumber())
                .date(dto.getDate())
                .time(dto.getTime())
                .venue(dto.getVenue())
                .type(dto.getType())
                .status(dto.getStatus())
                .isDouble(dto.getIsDouble())
                .minStake(dto.getMinStake())
                .resultType(dto.getResultType())
                .resultMargin(dto.getResultMargin())
                .homeScore(dto.getHomeScore())
                .awayScore(dto.getAwayScore())
                .homeOvers(dto.getHomeOvers())
                .awayOvers(dto.getAwayOvers())
                .defaultBetsAdded(false)
                .isUpdated(dto.getIsUpdated())
                .maxDoubleAmt(dto.getMaxDoubleAmt())
                .build();
    }

    public static MatchEntity updateDTOtoEntity(MatchRequestDTO dto,
                                                MatchEntity match) {
        match.setStatus(dto.getStatus());
        match.setIsDouble(dto.getIsDouble());
        match.setResultType(dto.getResultType());
        match.setResultMargin(dto.getResultMargin());
        match.setHomeScore(dto.getHomeScore());
        match.setAwayScore(dto.getAwayScore());
        match.setHomeOvers(dto.getHomeOvers());
        match.setAwayOvers(dto.getAwayOvers());
        return match;
    }

    public static MatchDTO mapEntitytoDTO(MatchEntity match) {
        if(match == null) return null;
        return MatchDTO.builder()
                .number(match.getNumber())
                .homeTeam(TeamMapper.mapEntityToShortDTO(match.getHomeTeamEntity()))
                .awayTeam(TeamMapper.mapEntityToShortDTO(match.getAwayTeamEntity()))
                .winner(TeamMapper.mapEntityToShortDTO(match.getWinnerEntity()))
                .date(match.getDate())
                .time(match.getTime())
                .venue(match.getVenue())
                .type(match.getType())
                .status(match.getStatus())
                .isDouble(match.getIsDouble())
                .minStake(match.getMinStake())
                .resultType(match.getResultType())
                .resultMargin(match.getResultMargin())
                .homeScore(match.getHomeScore())
                .awayScore(match.getAwayScore())
                .homeOvers(match.getHomeOvers())
                .awayOvers(match.getAwayOvers())
                .hasStarted(match.hasMatchStarted())
                .hasDoubleCutoffPassed(match.hasDoubleCutoffPassed())
                .hasEntryCutoffPassed(match.hasEntryCutoffPassed())
                .defaultBetsAdded(match.getDefaultBetsAdded())
                .isUpdated(match.getIsUpdated())
                .maxDoubleAmt(match.getMaxDoubleAmt())
                .build();
    }

    public static MatchShortDTO mapEntitytoShortDTO(MatchEntity match) {
        if (match == null) return null;
        return MatchShortDTO.builder()
                .number(match.getNumber())
                .homeTeam(TeamMapper.mapEntityToShortDTO(match.getHomeTeamEntity()))
                .awayTeam(TeamMapper.mapEntityToShortDTO(match.getAwayTeamEntity()))
                .winner(TeamMapper.mapEntityToShortDTO(match.getWinnerEntity()))
                .status(match.getStatus())
                .type(match.getType())
                .hasStarted(match.hasMatchStarted())
                .hasDoubleCutoffPassed(match.hasDoubleCutoffPassed())
                .hasEntryCutoffPassed(match.hasEntryCutoffPassed())
                .build();
    }

    public static TeamRequestDTO mapEntitytoTeamRequestDTO(MatchEntity match, Boolean isHomeTeam) {
        TeamScoreDTO team1Score = null;
        TeamScoreDTO team2Score = null;

        if (match.isCompleted()) {
            team1Score = TeamUtils.extractScoreForMatch(
                    match.getHomeScore(),
                    match.getHomeOvers()
            );
            team2Score = TeamUtils.extractScoreForMatch(
                    match.getAwayScore(),
                    match.getAwayOvers()
            );
        }

        return TeamRequestDTO.builder()
                .team(isHomeTeam
                        ? match.getHomeTeamEntity().getShortName()
                        : match.getAwayTeamEntity().getShortName())
                .isCompleted(match.isCompleted())
                .isWinner(isHomeTeam
                        ? match.getHomeTeamEntity().equals(match.getWinnerEntity())
                        : match.getAwayTeamEntity().equals(match.getWinnerEntity()))
                .forScore(isHomeTeam?team1Score:team2Score)
                .againstScore(isHomeTeam?team2Score:team1Score)
                .build();
    }
}
