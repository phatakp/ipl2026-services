package com.phatakp.ipl_services.teams.utils;

import com.phatakp.ipl_services.teams.dtos.TeamScoreDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TeamUtils {
    public static TeamScoreDTO extractScoreForMatch(String score,
                                                    String overs) {
        var result = new TeamScoreDTO();
        var ovs = overs.split("\\.");
        var wickets = score.split("/")[1];
        var balls = Integer.parseInt(wickets) == 10
                ? 120
                : Integer.parseInt(ovs[0]) * 6 + Integer.parseInt(ovs[1]);
        result.setBalls(balls);

        var runs = score.split("/");
        result.setRuns(Integer.parseInt(runs[0]));
        return result;
    }

    public static float calculateNrr(Integer forRuns,
                                     Integer forBalls,
                                     Integer againstRuns,
                                     Integer againstBalls) {

        return (forBalls > 0 && againstBalls > 0)
                ? (6 * (((float) forRuns / forBalls) - ((float) againstRuns / againstBalls)))
                : 0.00f;
    }

}
