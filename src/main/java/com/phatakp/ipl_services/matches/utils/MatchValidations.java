package com.phatakp.ipl_services.matches.utils;

import com.phatakp.ipl_services.config.exceptions.APIException;
import com.phatakp.ipl_services.matches.MatchRepository;
import com.phatakp.ipl_services.matches.models.MatchEntity;
import com.phatakp.ipl_services.matches.models.MatchStatus;
import com.phatakp.ipl_services.teams.models.TeamEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchValidations {
    private final MatchRepository matchRepository;

    public MatchEntity validateByNumber(Integer matchNum) {
        var match = matchRepository.getMatchByNumber(matchNum).orElse(null);
        if (match == null) {
            throw APIException.notFound("Match not found");
        }
        return match;
    }

    public static void validateDefaultBets(MatchEntity match) {
        if (!match.getDefaultBetsAdded()) {
            throw APIException.invalidData("Add default bets first");
        }
    }

    public static void validateIsUpdated(MatchEntity match) {
        if (!match.getIsUpdated()) {
            throw APIException.invalidData("Match has not been updated");
        }
    }

    public static void validateWinner(MatchStatus status, TeamEnum winner) {
        if (status.equals(MatchStatus.COMPLETED) && winner == null) {
            throw APIException.invalidData("Winner not found");
        }

        if (!status.equals(MatchStatus.COMPLETED) && winner != null) {
            throw APIException.invalidData("Winner invalid for match");
        }
    }
}
