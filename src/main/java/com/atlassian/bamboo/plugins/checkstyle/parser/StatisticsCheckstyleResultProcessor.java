package com.atlassian.bamboo.plugins.checkstyle.parser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

/**
 * A processor that only collects basic statistics about checkstyle errors.
 */
@NotThreadSafe
public class StatisticsCheckstyleResultProcessor implements CheckstyleResultProcessor {

    private long totalViolations;
    private long errorPriorityViolations;
    private long warningPriorityViolations;
    private long infoPriorityViolations;

    private final Map<String, Integer> violationsPerFile = Maps.newHashMap();

    @Override
    public void onError(@Nonnull CheckstyleError error) {
        incrementTotalViolations();
        incrementViolationsPerSeverity(error);
        incrementViolationsPerFile(error);
    }

    public long getTotalViolations() {
        return totalViolations;
    }

    public long getInfoPriorityViolations() {
        return infoPriorityViolations;
    }

    public long getWarningPriorityViolations() {
        return warningPriorityViolations;
    }

    public long getErrorPriorityViolations() {
        return errorPriorityViolations;
    }

    public Map<String, Integer> getViolationsPerFile() {
        return ImmutableMap.copyOf(violationsPerFile);
    }

    public int getViolationsPerFile(String fileName) {
        return violationsPerFile.containsKey(fileName) ? violationsPerFile.get(fileName) : 0;
    }

    private void incrementViolationsPerFile(CheckstyleError error) {
        violationsPerFile.put(error.getFileName(), getViolationsPerFile(error.getFileName()) + 1);
    }

    private void incrementViolationsPerSeverity(CheckstyleError error) {
        switch (error.getSeverity()) {
            case ERROR:
                errorPriorityViolations++;
                break;
            case WARNING:
                warningPriorityViolations++;
                break;
            case INFO:
                infoPriorityViolations++;
                break;
        }
    }

    private void incrementTotalViolations() {
        totalViolations++;
    }
}
