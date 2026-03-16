package com.phatakp.ipl_services.teams;

import com.phatakp.ipl_services.teams.dtos.TeamFormDTO;
import com.phatakp.ipl_services.teams.models.TeamEntity;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import com.phatakp.ipl_services.users.dtos.UserFormDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, TeamEnum> {

    @Query(value = "select t from TeamEntity t " +
            "left join fetch t.predictions " +
            "left join fetch t.homeMatches " +
            "left join fetch t.awayMatches " +
            "left join fetch t.winnerMatches " +
            "order by t.points desc, t.nrr desc ")
    List<TeamEntity> getStandings();

    @Query(value="select t.short_name, " +
            "case " +
            "when m.status='COMPLETED' and m.winner=t.short_name then 'WON' " +
            "when m.status='COMPLETED' and m.winner!=t.short_name then 'LOST' " +
            "else 'DRAW' " +
            "end as status " +
            "from teams t ,matches m " +
            "where (m.home_team=t.short_name " +
            "or m.away_team=t.short_name ) " +
            "and t.short_name = :team " +
            "and m.status <> 'SCHEDULED' " +
            "order by m.number desc limit 3",nativeQuery = true)
    List<TeamFormDTO> getTeamForm(@Param("team") String team);
}
