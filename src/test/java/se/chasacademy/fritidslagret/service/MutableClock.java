package se.chasacademy.fritidslagret.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

final class MutableClock extends Clock {
    private Instant instant;
    private final ZoneId zone;

    MutableClock(LocalDate date) {
        this(date, ZoneOffset.UTC);
    }

    private MutableClock(LocalDate date, ZoneId zone) {
        this.zone = zone;
        this.instant = date.atStartOfDay(zone).toInstant();
    }

    void setDate(LocalDate date) {
        this.instant = date.atStartOfDay(zone).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        MutableClock clock = new MutableClock(LocalDate.ofInstant(instant, newZone), newZone);
        clock.instant = instant;
        return clock;
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
