package com.atlassian.bamboo.plugins.checkstyle.parser;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Integer.parseInt;

public class CheckstyleError {

    public static final String LINE_XPATH = "@line";
    public static final String COLUMN_XPATH = "@column";
    public static final String SEVERITY_XPATH = "@severity";
    public static final String MESSAGE_XPATH = "@message";

    private static final int MISSING_VALUE = -1;

    private final String fileName;
    private final int line;
    private final int column;
    private final CheckstyleSeverity severity;
    private final String message;

    CheckstyleError(@Nonnull String fileName, int line, int column,
                    @Nonnull CheckstyleSeverity severity, @Nonnull String message) {
        this.fileName = checkNotNull(fileName, "fileName").replaceAll("\\\\", "/");
        this.line = line;
        this.column = column;
        this.severity = checkNotNull(severity, "severity");
        this.message = checkNotNull(message, "message");
    }

    public static CheckstyleError fromNode(@Nonnull String fileName, @Nonnull Node errorNode) {
        return new CheckstyleError(fileName,
                parseIntValue(errorNode.valueOf(LINE_XPATH)),
                parseIntValue(errorNode.valueOf(COLUMN_XPATH)),
                CheckstyleSeverity.parse(errorNode.valueOf(SEVERITY_XPATH)),
                errorNode.valueOf(MESSAGE_XPATH)
        );
    }

    @Nonnull
    public String getFileName() {
        return fileName;
    }

    public int getLine() {
        return line;
    }

    public boolean hasLine() {
        return line != MISSING_VALUE;
    }

    public int getColumn() {
        return column;
    }

    public boolean hasColumn() {
        return column != MISSING_VALUE;
    }

    @Nonnull
    public CheckstyleSeverity getSeverity() {
        return severity;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    private static int parseIntValue(String value) {
        if (StringUtils.isBlank(value)) {
            return MISSING_VALUE;
        }
        try {
            return parseInt(value);
        } catch(NumberFormatException e) {
            return MISSING_VALUE;
        }
    }
}
