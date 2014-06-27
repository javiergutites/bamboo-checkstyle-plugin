package com.atlassian.bamboo.plugins.checkstyle.parser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public enum CheckstyleSeverity {

    IGNORE,
    INFO,
    WARNING,
    ERROR;

    public static final CheckstyleSeverity DEFAULT = ERROR;

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public static CheckstyleSeverity parse(@Nullable String source) {
        try {
            return isNotBlank(source) ? valueOf(source.toUpperCase()) : DEFAULT;
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
}
