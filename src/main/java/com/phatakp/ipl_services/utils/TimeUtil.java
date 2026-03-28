package com.phatakp.ipl_services.utils;

import com.phatakp.ipl_services.matches.utils.MatchUtils;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TimeUtil {
    public static ZonedDateTime getCurrentISTTime() {
        // 1. Define the specific time zone for India Standard Time (IST)
        // Use "Asia/Kolkata" as "IST" is an abbreviation that can be ambiguous
        ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
        return ZonedDateTime.now(istZoneId);
    }

    public static boolean isTeamChgNotAllowed(){
        var currentISTTime = TimeUtil.getCurrentISTTime();
        var allowedTime = ZonedDateTime.parse("2026-05-08T00:00:00+05:30[Asia/Kolkata]");
        return currentISTTime.isAfter(allowedTime);
    }
}
