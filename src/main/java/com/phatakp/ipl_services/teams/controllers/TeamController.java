package com.phatakp.ipl_services.teams.controllers;

import com.phatakp.ipl_services.teams.dtos.TeamFormDTO;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.teams.services.TeamService;
import com.phatakp.ipl_services.teams.dtos.TeamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Teams Module")
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @Operation(summary = "Get list of team standings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TeamDTO.class))}),
    })
    @GetMapping
    public ResponseEntity<List<TeamDTO>> getTeamStandings() {
        return ResponseEntity.ok(teamService.getTeams());
    }

    @Operation(summary = "Get form of teams")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = TeamFormDTO.class))}),
    })
    @GetMapping("/form/{team}")
    public ResponseEntity<List<TeamFormDTO>> getTeamForm(@Parameter(description = "Shortname of team to be retrieved",required = true)
                                                             @PathVariable TeamEnum team) {
        return ResponseEntity.ok(teamService.getTeamForms(team));
    }

}
