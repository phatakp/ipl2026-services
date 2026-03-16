package com.phatakp.ipl_services.matches.controllers;

import com.phatakp.ipl_services.matches.dtos.MatchDTO;
import com.phatakp.ipl_services.matches.dtos.MatchRequestDTO;
import com.phatakp.ipl_services.matches.services.MatchService;
import com.phatakp.ipl_services.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Matches Admin Module")
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchAdminController {
    private final MatchService matchService;
    private final UserService userService;

    @Operation(summary = "Create Match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PostMapping
    public ResponseEntity<MatchDTO> createMatch(
            @Valid @RequestBody MatchRequestDTO request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        userService.assertIsAdminAction(authentication.getName(), "create match");
        return ResponseEntity.ok(matchService.addNewMatch(request));
    }




    @Operation(summary = "Update Match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MatchDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PutMapping
    public ResponseEntity<MatchDTO> updateMatch(
            @Valid @RequestBody MatchRequestDTO request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        userService.assertIsAdminAction(authentication.getName(), "update match");
        return ResponseEntity.ok(matchService.updateMatch(request));
    }






    @Operation(summary = "Add default predictions for Match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PutMapping("/default/{matchNum}")
    public void defaultPredictionsForMatch(
            @Parameter(description = "Number for match",required = true)
            @PathVariable Integer matchNum
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        userService.assertIsAdminAction(authentication.getName(), "default predictions for match");
        matchService.addDefaultPredictionsForMatch(matchNum);
    }




    @Operation(summary = "Reverse calculations for Match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PutMapping("/reverse/{matchNum}")
    public void reversePredictionsForMatch(
            @Parameter(description = "Number for match",required = true)
            @PathVariable Integer matchNum
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        userService.assertIsAdminAction(authentication.getName(), "reverse calculation for match");
        matchService.reversePredictionsForMatch(matchNum);
    }
}
