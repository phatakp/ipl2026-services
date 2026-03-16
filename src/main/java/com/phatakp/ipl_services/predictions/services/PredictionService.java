package com.phatakp.ipl_services.predictions.services;

import com.phatakp.ipl_services.predictions.dtos.DoubleRequestDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionRequestDTO;

import java.util.List;

public interface PredictionService {
    PredictionDTO addNewPrediction(PredictionRequestDTO request, String currentUserId);
    PredictionDTO updatePrediction(String id,PredictionRequestDTO request,String currentUserId);
    List<PredictionDTO> getMatchPredictions(Integer matchNum,String currentUserId);
    List<PredictionDTO> getUserPredictions(String userId,String currentUserId);
    void playDouble(String id, DoubleRequestDTO request, String currentUserId);
    PredictionDTO getUserPredictionForMatch(String userId, Integer matchNum);
}
