
package com.arsenal.framework.model.utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Gordon.Gan
 */
public class JodaTimeUtils {

    public static final long HOURS_PER_DAY = 24;

    public static final long MINUTES_PER_HOUR = 60;
    public static final long MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;

    public static final long SECONDS_PER_MINUTE = 60;
    public static final long SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
    public static final long SECONDS_PER_DAY = MINUTES_PER_DAY * SECONDS_PER_MINUTE;

    public static final long MILLISECONDS_PER_SECOND = 1000;
    public static final long MILLISECONDS_PER_MINUTE = SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
    public static final long MILLISECONDS_PER_HOUR = SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;
    public static final long MILLISECONDS_PER_DAY = SECONDS_PER_DAY * MILLISECONDS_PER_SECOND;

    public static final long MICROSECONDS_PER_SECOND = 1000 * MILLISECONDS_PER_SECOND;

    public static final long NANOSECONDS_PER_MICROSECOND = 1000;
    public static final long NANOSECONDS_PER_MILLISECOND = 1000 * NANOSECONDS_PER_MICROSECOND;

    public static DateTimeFormatter DEFAULT_UTC_DATE_TIME_FORMAT_OFFSET = DateTimeFormat.forPattern(
            "yyyy-MM-dd'T'HH:mm:ssZZ");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeZone CST_TIMEZONE = DateTimeZone.forID("Asia/Shanghai");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static DateTime now() {
        return DateTime.now(CST_TIMEZONE);
    }

    public static String toDateFormat(DateTime dateTime) {
        return DATE_FORMAT.print(dateTime);
    }

    public static String toDateTimeFormat(DateTime dateTime) {
        return DATETIME_FORMAT.print(dateTime);
    }

    public static LocalDateTime toLocalDateTime(Long timestampOfSecond) {
        Instant instant = Instant.ofEpochSecond(timestampOfSecond);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));
        return localDateTime;
    }
}
