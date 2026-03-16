package com.phatakp.ipl_services.predictions.controllers;


import com.phatakp.ipl_services.config.exceptions.APIException;
import com.phatakp.ipl_services.predictions.dtos.DoubleRequestDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionRequestDTO;
import com.phatakp.ipl_services.predictions.services.PredictionService;
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

import java.util.List;

@Tag(name = "Predictions Module")
@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @Operation(summary = "Get list of predictions for match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PredictionDTO.class))}),
    })
    @GetMapping("/match/{matchNum}")
    public ResponseEntity<List<PredictionDTO>> getMatchPredictions(
            @Parameter(description = "Number for the match",required = true)
            @PathVariable Integer matchNum
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return ResponseEntity.ok(predictionService.getMatchPredictions(matchNum,authentication.getName()));
    }

    @Operation(summary = "Get list of predictions for user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PredictionDTO.class))}),
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PredictionDTO>> getUserPredictions(
            @Parameter(description = "id of the user",required = true)
            @PathVariable String userId
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return ResponseEntity.ok(predictionService.getUserPredictions(userId,authentication.getName()));
    }

    @Operation(summary = "Get predictions for logged in user for given match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PredictionDTO.class))}),
    })
    @GetMapping("/curr-user/match/{matchNum}")
    public ResponseEntity<PredictionDTO> getUserPredictionForMatch(
            @Parameter(description = "number for match",required = true)
            @PathVariable Integer matchNum
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return ResponseEntity.ok(predictionService.getUserPredictionForMatch(authentication.getName(),matchNum));
    }



    @Operation(summary = "Create New Prediction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PredictionRequestDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PostMapping
    public ResponseEntity<PredictionDTO> createPrediction(
            @Valid @RequestBody PredictionRequestDTO request
    ) throws APIException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return ResponseEntity.ok(predictionService.addNewPrediction(request, authentication.getName()));
    }



    @Operation(summary = "Update Prediction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PredictionRequestDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PutMapping("/{id}")
    public ResponseEntity<PredictionDTO> updatePrediction(
            @Parameter(description = "id of the prediction",required = true)
            @PathVariable String id,
            @Valid @RequestBody PredictionRequestDTO request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return ResponseEntity.ok(predictionService.updatePrediction(id,request, authentication.getName()));
    }


    @Operation(summary = "Play Double Prediction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data in input", content = @Content),
    })
    @PutMapping("/double/{id}")
    public ResponseEntity<Void> doublePrediction(
            @Parameter(description = "id of the prediction",required = true)
            @PathVariable String id,
            @Valid @RequestBody DoubleRequestDTO request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        try{
            predictionService.playDouble(id, request, authentication.getName());
        } catch (Exception e){
            throw APIException.invalidData(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }



}
