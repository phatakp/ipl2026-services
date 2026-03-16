package com.phatakp.ipl_services.predictions;

import com.phatakp.ipl_services.predictions.models.PredictionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<PredictionEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select p from PredictionEntity p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.match " +
            "left join fetch p.user " +
            "where p.match.number=:matchNum " +
            "order by p.updatedAt desc")
    List<PredictionEntity> findByMatchNum(@Param("matchNum") Integer matchNum);

    @Query(value = "select p from PredictionEntity p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.match " +
            "left join fetch p.user " +
            "where p.user.clerkId=:userId " +
            "order by p.match.number desc")
    List<PredictionEntity> findByUserId(@Param("userId") String userId);


    @Query(value = "select p from PredictionEntity p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.match " +
            "left join fetch p.user " +
            "where p.id=:id")
    Optional<PredictionEntity> getPredictionById(@Param("id") String id);

    @Query(value = "select p from PredictionEntity p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.match " +
            "left join fetch p.user " +
            "where p.user.clerkId=:userId " +
            "and p.match.number=:matchNum")
    Optional<PredictionEntity> getUserPredictionForMatch(@Param("userId") String userId,
                                                         @Param("matchNum") Integer matchNum);



}
