package com.phatakp.ipl_services.matches.controllers;


import com.phatakp.ipl_services.matches.dtos.MatchDTO;
import com.phatakp.ipl_services.matches.dtos.PredictedWinnerDTO;
import com.phatakp.ipl_services.matches.services.MatchService;
import com.phatakp.ipl_services.teams.models.TeamEnum;
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

@Tag(name = "Matches Module")
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "Get list of fixtures")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
    })
    @GetMapping("/fixtures")
    public ResponseEntity<List<MatchDTO>> getFixtures() {
        return ResponseEntity.ok(matchService.getFixtures());
    }


    @Operation(summary = "Get list of results")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
    })
    @GetMapping("/results")
    public ResponseEntity<List<MatchDTO>> getResults() {
        return ResponseEntity.ok(matchService.getResults());
    }


    @Operation(summary = "Get match by number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
    })
    @GetMapping("/{matchNum}")
    public ResponseEntity<MatchDTO> getMatchByNum(
            @Parameter(description = "Number of the match to be retrieved", required = true)
            @PathVariable Integer matchNum
    ) {
        return ResponseEntity.ok(matchService.getMatchByNum(matchNum));
    }


    @Operation(summary = "Get fixtures by team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
    })
    @GetMapping("/fixtures/{team}")
    public ResponseEntity<List<MatchDTO>> getFixturesByTeam(
            @Parameter(description = "Shortname for Team", required = true)
            @PathVariable TeamEnum team
    ) {
        return ResponseEntity.ok(matchService.getFixturesByTeam(team));
    }


    @Operation(summary = "Get results by team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
    })
    @GetMapping("/results/{team}")
    public ResponseEntity<List<MatchDTO>> getResultsByTeam(
            @Parameter(description = "Shortname for Team", required = true)
            @PathVariable TeamEnum team
    ) {
        return ResponseEntity.ok(matchService.getResultsByTeam(team));
    }



    @Operation(summary = "Get predicted winner amount by team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PredictedWinnerDTO.class))}),
    })
    @GetMapping("/predict-winner/{matchNum}")
    public ResponseEntity<List<PredictedWinnerDTO>> getPredictionTeamWinner(
            @Parameter(description = "Number for Match", required = true)
            @PathVariable Integer matchNum
    ) {
        return ResponseEntity.ok(matchService.getPredictionTeamWinner(matchNum));
    }


}
