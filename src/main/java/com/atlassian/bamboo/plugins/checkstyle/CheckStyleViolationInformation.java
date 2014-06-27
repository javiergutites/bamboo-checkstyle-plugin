/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * CheckStyleViolationInformation.
 *
 * @author Stephan Paulicke
 */
public class CheckStyleViolationInformation implements Comparable<CheckStyleViolationInformation> {

    private final String fileName;
    private final Integer numberOfViolations;

    public CheckStyleViolationInformation(@Nonnull String fileName, int numberOfViolations) {
        this.fileName = checkNotNull(fileName, "fileName");
        this.numberOfViolations = numberOfViolations;
    }

    public int compareTo(CheckStyleViolationInformation other) {
        return Integer.compare(this.numberOfViolations, other.numberOfViolations);
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getNumberOfViolations() {
        return numberOfViolations;
    }
}