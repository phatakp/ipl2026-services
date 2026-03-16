package com.phatakp.ipl_services.utils;

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
}
