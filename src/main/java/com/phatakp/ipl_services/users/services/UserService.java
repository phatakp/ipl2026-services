package com.phatakp.ipl_services.users.services;

import com.phatakp.ipl_services.users.dtos.UserDTO;
import com.phatakp.ipl_services.users.dtos.UserFormDTO;
import com.phatakp.ipl_services.users.dtos.UserRequestDTO;
import com.phatakp.ipl_services.users.models.UserEntity;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    List<UserEntity> getAllUserEntity();
    UserDTO getUserById(String id);
    UserDTO upsertUser(UserRequestDTO request);
    void activateUser(String userId);
    void assertIsAdminAction(String userId,String action);
    void assertIsOwnerAction(String loggedInUserId, String userId,String action);
    UserEntity validateUserId(String userId);
    List<UserEntity> getDefaultUsersForMatch(Integer matchNum);
    List<UserFormDTO> getUserForm(String userId);
    Integer getUsersCount();
}
