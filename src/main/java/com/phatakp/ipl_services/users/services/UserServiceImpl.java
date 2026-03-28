package com.phatakp.ipl_services.users.services;

import com.phatakp.ipl_services.config.AppProperties;
import com.phatakp.ipl_services.config.exceptions.APIException;
import com.phatakp.ipl_services.predictions.utils.PredictionMapper;
import com.phatakp.ipl_services.teams.services.TeamService;
import com.phatakp.ipl_services.users.UserRepository;
import com.phatakp.ipl_services.users.dtos.UserDTO;
import com.phatakp.ipl_services.users.dtos.UserFormDTO;
import com.phatakp.ipl_services.users.dtos.UserRequestDTO;
import com.phatakp.ipl_services.users.models.UserEntity;
import com.phatakp.ipl_services.users.models.UserRole;
import com.phatakp.ipl_services.users.utils.UserMapper;
import com.phatakp.ipl_services.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final AppProperties appProperties;

    /**
     * @return List of UserDTO
     */
    @Override
    public List<UserDTO> getAllUsers() {
        if (appProperties.getDebug()) {
            log.info("getAllUsersWithTeam()");
        }
        return userRepository.getAllUsersWithTeam();
    }

    /**
     * @return List of UserEntity
     */
    @Override
    public List<UserEntity> getAllUserEntity() {
        if (appProperties.getDebug()) {
            log.info("getAllUserEntity()");
        }
        return userRepository.findAll();
    }

    /**
     * @param id user id
     * @return UserDTO
     */
    @Override
    public UserDTO getUserById(String id) {
        if (appProperties.getDebug()) {
            log.info("getUserById()");
        }
        return userRepository.getUserByClerkId(id).orElse(null);
    }

    /**
     * @param request input details for user
     * @return UserDTO
     */
    @Override
    @Transactional
    public UserDTO upsertUser(UserRequestDTO request) {

        var team = teamService.validateTeam(request.getTeam(),"Team not found");

        var existingUser = userRepository.findByClerkId(request.getClerkId()).orElse(null);
        if (existingUser != null) {
            if (appProperties.getDebug()) {
                log.info("updateUser()");
            }
            // Update team allowed only till completion of match 50
            if (!existingUser.getTeamEntity().getShortName().equals(team.getShortName()) && TimeUtil.isTeamChgNotAllowed()) {
                throw APIException.invalidData("Team change is not allowed");
            }

            // Update user detail
            var user = UserMapper.updateUserDTOtoUserEntity(request,existingUser, team);
            var predictionOptional = existingUser.getPredictions()
                    .stream()
                    .filter(p->p.getMatch()==null)
                    .findFirst();
            var restPredictions = new ArrayList<>(
                    existingUser.getPredictions()
                    .stream()
                    .filter(p -> p.getMatch() != null).toList());
            if (predictionOptional.isPresent()) {
                var prediction = predictionOptional.get();
                prediction.setTeamEntity(team);
                restPredictions.add(prediction);
                user.setPredictions(restPredictions);
            }
            var savedUser = userRepository.save(user);
            return UserMapper.mapUserEntityToUserDTO(savedUser);
        }

        if (appProperties.getDebug()) {
            log.info("createUser()");
        }

        //Create user
        var user = UserMapper.mapUserDTOtoUserEntity(request,team);
        var prediction = PredictionMapper.getIPLWinnerPrediction(team,user);
        user.setPredictions(List.of(prediction));
        var savedUser = userRepository.save(user);
        return UserMapper.mapUserEntityToUserDTO(savedUser);
    }

    /**
     * @param userId id of user
     */
    @Override
    public void activateUser(String userId) {
        if (appProperties.getDebug()) {
            log.info("activateUser()");
        }
        var user = userRepository.findByClerkId(userId).orElse(null);
        if (user == null) {
            throw  APIException.notFound("User not found");
        }
        user.setIsActive(true);
        userRepository.save(user);
    }


    /**
     * @param userId logged-in user id
     * @param action action performed
     */
    @Override
    public void assertIsAdminAction(String userId, String action) {
        if (appProperties.getDebug()) {
            log.info("assertAdminUser()");
        }
        if (!userRepository.existsByClerkIdAndRole(userId, UserRole.ADMIN)){
            throw APIException.unAuthorized(action);
        }
    }

    /**
     * @param loggedInUserId userId of logged-in user
     * @param userId userId on the record
     * @param action action performed
     */
    @Override
    public void assertIsOwnerAction(String loggedInUserId, String userId, String action) {
        if (appProperties.getDebug()) {
            log.info("assertIsOwnerUser()");
        }
        if (!loggedInUserId.equals(userId)){
            throw APIException.unAuthorized(action);
        }
    }

    /**
     * @param userId input user id
     * @return validated UserEntity
     */
    @Override
    public UserEntity validateUserId(String userId) {
        if (appProperties.getDebug()) {
            log.info("validateUserId()");
        }
        var user = userRepository.findByClerkId(userId).orElse(null);
        if(user == null){
            throw APIException.notFound("User not found: " + userId);
        }
        return user;
    }

    /**
     * @param matchNum number for match
     * @return List of UserEntity
     */
    @Override
    public List<UserEntity> getDefaultUsersForMatch(Integer matchNum) {
        if (appProperties.getDebug()) {
            log.info("getDefaultUsersForMatch()");
        }
        return userRepository.getDefaultUsersForMatch(matchNum);
    }

    /**
     * @param userId id of user
     * @return list of UserFormDTO
     */
    @Override
    public List<UserFormDTO> getUserForm(String userId) {
        return userRepository.getUserForm(userId);

    }

    /**
     * @return count of users
     */
    @Override
    public Integer getUsersCount() {
        return userRepository.getUsersCount();
    }
}
