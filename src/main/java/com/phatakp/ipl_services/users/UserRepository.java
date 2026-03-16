package com.phatakp.ipl_services.users;

import com.phatakp.ipl_services.users.dtos.UserDTO;
import com.phatakp.ipl_services.users.dtos.UserFormDTO;
import com.phatakp.ipl_services.users.models.UserEntity;
import com.phatakp.ipl_services.users.models.UserRole;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Boolean existsByClerkIdAndRole(String clerkId, UserRole role);

    @Query(value = "select u.clerk_id, u.email," +
            "u.first_name,u.last_name,u.image_url," +
            "u.role, u.team, u.balance, u.doubles_left," +
            "DENSE_RANK() OVER (ORDER BY u.balance DESC) as rank, " +
            "u.is_active " +
            "from users u " +
            "join teams t " +
            "on u.team=t.short_name " +
            "where u.clerk_id=:id",nativeQuery = true)
    Optional<UserDTO> getUserByClerkId(@Param("id") String id);

    @Query(value = "select u.clerk_id, u.email," +
            "u.first_name,u.last_name,u.image_url," +
            "u.role, u.team, u.balance, u.doubles_left," +
            "DENSE_RANK() OVER (ORDER BY u.balance DESC) as rank, " +
            "u.is_active " +
            "from users u " +
            "join teams t " +
            "on u.team=t.short_name " +
            "order by u.balance desc",nativeQuery = true)
    List<UserDTO> getAllUsersWithTeam();

    Optional<UserEntity> findByClerkId(@NotNull String clerkId);

    @Query(value = "select u.* from users u " +
            "left join predictions p " +
            "on p.user_id=u.clerk_id " +
            "and p.match_number = :matchNum " +
            "where p.team is null", nativeQuery = true)
    List<UserEntity> getDefaultUsersForMatch(@Param("matchNum") Integer matchNum);

    @Query(value="select u.clerk_id, p.status from users u " +
            "join predictions p " +
            "on u.clerk_id=p.user_id " +
            "where u.clerk_id =:userId " +
            "and p.status not in ('PLACED','DEFAULT') " +
            "order by p.match_number desc limit 5",nativeQuery = true)
    List<UserFormDTO> getUserForm(@Param("userId") String userId);

    @Query(value = "select count(clerkId) from UserEntity ")
    Integer getUsersCount();
}
