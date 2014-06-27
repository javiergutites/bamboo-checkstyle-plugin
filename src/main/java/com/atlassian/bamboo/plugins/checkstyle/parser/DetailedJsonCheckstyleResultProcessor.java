package com.atlassian.bamboo.plugins.checkstyle.parser;

import com.atlassian.stash.codeanalysis.rest.CodeAnalysisNotification;
import com.atlassian.stash.codeanalysis.rest.CodeAnalysisNotificationLocator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;
import java.util.Map;

import static com.atlassian.stash.codeanalysis.rest.CodeAnalysisNotification.Severity;

/**
 * A processor that builts a detailed JSON entity containing all checkstyle notifications.
 */
@NotThreadSafe
public class DetailedJsonCheckstyleResultProcessor implements CheckstyleResultProcessor {

    private static final String CHECKSTYLE_TOOL = "Checkstyle";

    private final Map<String, List<CodeAnalysisNotification>> notifications = Maps.newHashMap();

    @Override
    public void onError(@Nonnull CheckstyleError error) {
        add(error.getFileName(), createNotification(error));
    }

    public Map<String, List<CodeAnalysisNotification>> getNotifications() {
        return ImmutableMap.copyOf(notifications);
    }

    private void add(String fileName, CodeAnalysisNotification notification) {
        if (!notifications.containsKey(fileName)) {
            notifications.put(fileName, Lists.<CodeAnalysisNotification>newArrayList());
        }
        notifications.get(fileName).add(notification);
    }

    private CodeAnalysisNotification createNotification(CheckstyleError error) {
        return new CodeAnalysisNotification.Builder()
                .tool(CHECKSTYLE_TOOL)
                .details(error.getMessage())
                .severity(getSeverity(error))
                .locator(createLocator(error))
                .build();
    }

    private CodeAnalysisNotificationLocator createLocator(CheckstyleError error) {
        if (error.hasLine() && error.hasColumn()) {
            return CodeAnalysisNotificationLocator.position(error.getLine(), error.getColumn());
        } else if (error.hasLine()) {
            return CodeAnalysisNotificationLocator.line(error.getLine());
        } else {
            return CodeAnalysisNotificationLocator.file();
        }
    }

    private Severity getSeverity(CheckstyleError error) {
        switch (error.getSeverity()) {
            case ERROR:
                return Severity.ERROR;
            case WARNING:
                return Severity.WARNING;
            case INFO:
                return Severity.INFO;
            case IGNORE:
                return Severity.INFO;
        }
        throw new AssertionError("New CheckstyleSeverity has been added and not supported here");
    }
}
