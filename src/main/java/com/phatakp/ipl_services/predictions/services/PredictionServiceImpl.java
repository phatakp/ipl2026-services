package com.phatakp.ipl_services.predictions.services;

import com.phatakp.ipl_services.config.AppProperties;
import com.phatakp.ipl_services.config.exceptions.APIException;
import com.phatakp.ipl_services.matches.services.MatchService;
import com.phatakp.ipl_services.predictions.PredictionRepository;
import com.phatakp.ipl_services.predictions.dtos.DoubleRequestDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionDTO;
import com.phatakp.ipl_services.predictions.dtos.PredictionRequestDTO;
import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import com.phatakp.ipl_services.predictions.utils.PredictionMapper;
import com.phatakp.ipl_services.teams.services.TeamService;
import com.phatakp.ipl_services.users.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionServiceImpl implements PredictionService {
    private final TeamService teamService;
    private final UserService userService;
    private final PredictionRepository predictionRepository;
    private final MatchService matchService;
    private final AppProperties appProperties;

    /**
     * @param request Details of match prediction
     * @return PredictionDTO
     */
    @Override
    @Transactional
    public PredictionDTO addNewPrediction(PredictionRequestDTO request, String currentUserId) {
        if (appProperties.getDebug()) {
            log.info("addNewPrediction()");
        }
        var match = matchService.validateMatchByNum(request.getMatchNumber());

        var user = userService.validateUserId(currentUserId);
        var team = teamService.validateTeam(request.getTeam(),"Team not found");
        var matchPredictions = getMatchPredictions(match.getNumber(),currentUserId);

        if (match.hasEntryCutoffPassed()) {
            throw APIException.invalidData("Entry Cutoff passed for match");
        }

        if (match.getTeams().stream().noneMatch(t->t.equals(team))){
            throw APIException.invalidData("Invalid team - " + team.getShortName());
        }

        if (matchPredictions.stream()
                .anyMatch(p->p.getUser().getClerkId().equals(user.getClerkId()))){
            throw APIException.alreadyPresent("Prediction already exists");
        }

        if (request.getAmount() < match.getMinStake()) {
            throw APIException.invalidData("Minimum amount required: "+ match.getMinStake());
        }

        if (!user.getIsActive()) {
            throw APIException.invalidData("User is not active");
        }

        var prediction = PredictionMapper.mapRequestDTOtoEntity(request);
        if (prediction.getAmount()*2 > match.getMaxDoubleAmt()) {
            match.setMaxDoubleAmt((short) (prediction.getAmount()*2));
        }
        prediction.setMatch(match);
        prediction.setUser(user);
        prediction.setTeamEntity(team);
        var savedPrediction = predictionRepository.save(prediction);
        return PredictionMapper.mapEntitytoDTO(savedPrediction);
    }

    /**
     * @param id id of prediction
     * @param request details of prediction
     * @return PredictionDTO
     */
    @Override
    @Transactional
    public PredictionDTO updatePrediction(String id, PredictionRequestDTO request, String currentUserId) {
        if (appProperties.getDebug()) {
            log.info("updatePrediction()");
        }
        var match = matchService.validateMatchByNum(request.getMatchNumber());
        var team = teamService.validateTeam(request.getTeam(),"Team not found");
        var prediction = predictionRepository.getPredictionById(id).orElse(null);
        if (prediction == null){
            throw APIException.notFound("Prediction not found");
        }

        var user = userService.validateUserId(currentUserId);

        userService.assertIsOwnerAction(currentUserId, user.getClerkId(),"Update prediction");

        if (!user.getIsActive()) {
            throw APIException.invalidData("User is not active");
        }

        // Cannot update after match starts
        if (match.hasMatchStarted()) {
            throw APIException.invalidData("Update Cutoff passed for match");
        }


        // Invalid team
        if (match.getTeams().stream().noneMatch(t->t.equals(team))){
            throw APIException.invalidData("Invalid team - " + team.getShortName());
        }

        // Cannot decrease amount
        if (prediction.getTeamEntity().equals(team) &&
                prediction.getAmount() > request.getAmount() && match.hasEntryCutoffPassed()) {
            throw APIException.invalidData("Amount cannot be decreased");
        }

        // Amount to be doubled for team update
        if (!prediction.getTeamEntity().equals(team) && match.hasEntryCutoffPassed() &&
                 request.getAmount() < prediction.getAmount()*2 ) {
            throw APIException.invalidData("Min Amount required "+ prediction.getAmount()*2);
        }

        prediction.setAmount(request.getAmount());
        prediction.setTeamEntity(team);
        if (prediction.getAmount()*2 > match.getMaxDoubleAmt()) {
            match.setMaxDoubleAmt((short) (prediction.getAmount()*2));
        }
        var savedPrediction = predictionRepository.save(prediction);
        return PredictionMapper.mapEntitytoDTO(savedPrediction);
    }

    /**
     * @param matchNum number for match
     * @return list of PredictionDTO
     */
    @Override
    @Transactional
    public List<PredictionDTO> getMatchPredictions(Integer matchNum,String currentUserId) {
        if (appProperties.getDebug()) {
            log.info("getMatchPredictions()");
        }
        var match =  matchService.validateMatchByNum(matchNum);
        var predictions = predictionRepository.findByMatchNum(matchNum);
        if (match.hasEntryCutoffPassed()) {
            return predictions.stream()
                    .map(PredictionMapper::mapEntitytoDTO).toList();
        }

        return predictions.stream()
                .filter(p->p.getUser().getClerkId().equals(currentUserId))
                .map(PredictionMapper::mapEntitytoDTO).toList();
    }

    /**
     * @param userId id of users
     * @return list of PredictionDTO
     */
    @Override
    public List<PredictionDTO> getUserPredictions(String userId, String currentUserId) {
        if (appProperties.getDebug()) {
            log.info("getUserPredictions()");
        }
        var predictions = predictionRepository.findByUserId(userId);

        if (currentUserId.equals(userId)) {
            return predictions.stream()
                    .map(PredictionMapper::mapEntitytoDTO).toList();
        }
        return predictions.stream()
                .filter(p-> p.getMatch() == null || p.getMatch().hasEntryCutoffPassed())
                .map(PredictionMapper::mapEntitytoDTO).toList();
    }

    /**
     * @param id of prediction
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void playDouble(String id, DoubleRequestDTO request, String currentUserId) {
        if (appProperties.getDebug()) {
            log.info("playDouble()");
        }
        var prediction = predictionRepository.getPredictionById(id).orElse(null);
        if (prediction == null){
            throw APIException.notFound("Prediction not found");
        }
        var user = prediction.getUser();
        var match = matchService.validateMatchByNum(prediction.getMatch().getNumber());
        var doublesLeft = user.getDoublesLeft();

        userService.assertIsOwnerAction(currentUserId, user.getClerkId(),"Double Play");

        if (!user.getIsActive()) {
            throw APIException.unAuthorized("User is not active");
        }

        // Cannot update before match starts
        if (!match.hasMatchStarted()) {
            throw APIException.invalidData("Double cannot be played");
        }

        // Cannot update after cutoff
        if (match.hasDoubleCutoffPassed()) {
            throw APIException.invalidData("Double Cutoff passed for match");
        }

        // Not enough doubles
        if (doublesLeft <=0 ) {
            throw APIException.invalidData("No more doubles left");
        }

        // Double Amount changed
        if (!Objects.equals(request.getDoubleAmt(), match.getMaxDoubleAmt())) {
            throw APIException.invalidData("Someone played double! Amount changed");
        }

        var matchPredictions = predictionRepository.findByMatchNum(
                prediction.getMatch().getNumber());
        var existingDoublePred = matchPredictions.stream()
                .filter(PredictionEntity::getIsDouble)
                .findFirst().orElse(null);
        if (existingDoublePred != null) {
            existingDoublePred.setIsDouble(false);
            var usr = existingDoublePred.getUser();
            var doubles = usr.getDoublesLeft();
            usr.setDoublesLeft((short) (doubles+1));
        }

        prediction.setIsDouble(true);
        prediction.setAmount(match.getMaxDoubleAmt());

        match.setIsDouble(Boolean.TRUE);
        match.setMaxDoubleAmt((short) (match.getMaxDoubleAmt()*2));
        prediction.setMatch(match);

        user.setDoublesLeft((short)(doublesLeft-1));
        prediction.setUser(user);

        predictionRepository.save(prediction);
    }

    /**
     * @param userId id of the user
     * @param matchNum number for match
     * @return PredictionDTO
     */
    @Override
    public PredictionDTO getUserPredictionForMatch(String userId, Integer matchNum) {
        var pred =  predictionRepository.getUserPredictionForMatch(userId,matchNum).orElse(null);
        return PredictionMapper.mapEntitytoDTO(pred);
    }
}
