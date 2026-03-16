package com.phatakp.ipl_services.users.utils;

import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.users.dtos.UserDTO;
import com.phatakp.ipl_services.users.dtos.UserRequestDTO;
import com.phatakp.ipl_services.users.dtos.UserShortDTO;
import com.phatakp.ipl_services.users.models.UserEntity;
import com.phatakp.ipl_services.users.models.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserDTO mapUserEntityToUserDTO(UserEntity user) {
        return UserDTO.builder()
                .clerkId(user.getClerkId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .balance(user.getBalance())
                .role(user.getRole().name())
                .doublesLeft(user.getDoublesLeft())
                .imageUrl(user.getImageUrl())
                .team(user.getTeamEntity().getShortName().name())
                .isActive(user.getIsActive())
                .build();
    }

    public static UserShortDTO mapUserEntityToShortDTO(UserEntity user) {
        return UserShortDTO.builder()
                .clerkId(user.getClerkId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .team(user.getTeamEntity().getShortName().name())
                .isActive(user.getIsActive())
                .build();
    }

    public static UserEntity mapUserDTOtoUserEntity(UserRequestDTO request, TeamEntity team) {
        return UserEntity.builder()
                .clerkId(request.getClerkId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .balance(request.getBalance())
                .role(UserRole.PLAYER)
                .doublesLeft(request.getDoublesLeft())
                .imageUrl(request.getImageUrl())
                .teamEntity(team)
                .isActive(request.getIsActive())
                .build();
    }

    public static UserEntity updateUserDTOtoUserEntity(UserRequestDTO request,
                                                       UserEntity user,
                                                       TeamEntity team) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBalance(request.getBalance());
        user.setDoublesLeft(request.getDoublesLeft());
        user.setTeamEntity(team);
        user.setIsActive(request.getIsActive());
        return user;
    }
}
