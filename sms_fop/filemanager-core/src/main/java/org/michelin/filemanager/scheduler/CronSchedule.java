package org.michelin.filemanager.scheduler;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.michelin.filemanager.config.ConfigValidationException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * A named cron expression with a next-fire calculator. Pure function over
 * (current instant) → (next fire instant). Thread-safe and immutable once
 * constructed; the underlying ExecutionTime is reused.
 *
 * Cron format: standard 5-field UNIX cron (min hour day month dow). Time zone
 * is supplied at construction so behavior is reproducible across JVMs whose
 * default zone differs.
 */
public final class CronSchedule {

    private static final CronParser PARSER =
            new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    private final String name;
    private final String expression;
    private final ExecutionTime executionTime;
    private final ZoneId zone;

    public CronSchedule(String name, String expression, ZoneId zone) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name is required");
        if (expression == null || expression.isBlank())
            throw new IllegalArgumentException("cron expression is required (use the empty-list factory to disable)");
        if (zone == null)
            throw new IllegalArgumentException("zone is required");

        this.name = name;
        this.expression = expression.trim();
        this.zone = zone;
        try {
            Cron cron = PARSER.parse(this.expression).validate();
            this.executionTime = ExecutionTime.forCron(cron);
        } catch (IllegalArgumentException e) {
            throw new ConfigValidationException(
                    "invalid cron expression for schedule '" + name + "': '" + expression + "' — " + e.getMessage());
        }
    }

    /** First fire strictly after {@code from}. Never returns equal to {@code from}. */
    public Instant nextFireAfter(Instant from) {
        ZonedDateTime zdt = from.atZone(zone);
        Optional<ZonedDateTime> next = executionTime.nextExecution(zdt);
        return next.map(ZonedDateTime::toInstant)
                .orElseThrow(() -> new IllegalStateException(
                        "cron-utils returned no next-fire for schedule '" + name +
                                "' after " + from + " — should not happen for unbounded UNIX cron"));
    }

    public String name() { return name; }
    public String expression() { return expression; }
    public ZoneId zone() { return zone; }

    @Override
    public String toString() {
        return "CronSchedule[" + name + " '" + expression + "' " + zone + "]";
    }
}
