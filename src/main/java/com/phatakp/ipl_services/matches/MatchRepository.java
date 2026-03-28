package com.phatakp.ipl_services.matches;

import com.phatakp.ipl_services.matches.dtos.MatchStatDTO;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Integer> {

    @Query(value = "select m from MatchEntity m " +
            "left join fetch m.homeTeamEntity " +
            "left join fetch m.awayTeamEntity " +
            "left join fetch m.winnerEntity " +
            "left join fetch m.predictions p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.user " +
            "where m.status='SCHEDULED' " +
            "order by m.number ASC")
    List<MatchEntity> getFixtures();

    @Query(value = "select m from MatchEntity m " +
            "left join fetch m.homeTeamEntity " +
            "left join fetch m.awayTeamEntity " +
            "left join fetch m.winnerEntity " +
            "left join fetch m.predictions p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.user " +
            "where m.status!='SCHEDULED' " +
            "order by m.number DESC")
    List<MatchEntity> getResults();


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select m from MatchEntity m " +
            "left join fetch m.homeTeamEntity " +
            "left join fetch m.awayTeamEntity " +
            "left join fetch m.winnerEntity " +
            "left join fetch m.predictions p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.user " +
            "where m.number=:matchNum ")
    Optional<MatchEntity> getMatchByNumber(@Param("matchNum") Integer matchNum);

    @Query(value = "select m from MatchEntity m " +
            "left join fetch m.homeTeamEntity " +
            "left join fetch m.awayTeamEntity " +
            "left join fetch m.winnerEntity " +
            "left join fetch m.predictions p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.user " +
            "where m.status='SCHEDULED' " +
            "and (m.homeTeamEntity.shortName=:team or " +
            "m.awayTeamEntity.shortName=:team) " +
            "order by m.number ASC")
    List<MatchEntity> getFixturesByTeam(@Param("team") TeamEnum team);

    @Query(value = "select m from MatchEntity m " +
            "left join fetch m.homeTeamEntity " +
            "left join fetch m.awayTeamEntity " +
            "left join fetch m.winnerEntity " +
            "left join fetch m.predictions p " +
            "left join fetch p.teamEntity " +
            "left join fetch p.user " +
            "where m.status != 'SCHEDULED' " +
            "and (m.homeTeamEntity.shortName=:team or " +
            "m.awayTeamEntity.shortName=:team) " +
            "order by m.number DESC")
    List<MatchEntity> getResultsByTeam(@Param("team") TeamEnum team);

    @Query(value = "select p.team, " +
            "cast(coalesce(sum(p.amount),0) as smallint) " +
            "from predictions p " +
            "where p.match_number=:matchNum " +
            "group by p.team  ", nativeQuery = true)
    List<MatchStatDTO> getStats(
            @Param("matchNum") Integer matchNum);

    @Query(value = "select p.team, " +
            "cast(coalesce(sum(p.amount),0) as smallint) " +
            "from predictions p " +
            "where p.match_number is null " +
            "group by p.team  ", nativeQuery = true)
    List<MatchStatDTO> getFinalStats();

    @Query(value = "select case " +
            "when p.team=:winner " +
            "then true else false end " +
            "from predictions p " +
            "where p.match_number=:matchNum " +
            "and p.is_double=true ",nativeQuery = true)
    boolean isDoubleWon(@Param("matchNum") Integer matchNum,
                        @Param("winner") String winner);
}
