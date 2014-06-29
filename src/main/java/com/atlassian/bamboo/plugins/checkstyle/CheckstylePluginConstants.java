package com.atlassian.bamboo.plugins.checkstyle;

import com.atlassian.bamboo.plugins.checkstyle.tasks.CheckStyleTaskConfigurator;

public final class CheckstylePluginConstants {

    private CheckstylePluginConstants() {
        throw new AssertionError(CheckstylePluginConstants.class.getName() + " should not be instantiated");
    }

    public static final String CHECKSTYLE_EXISTS = "custom.checkstyle.exists";

    public static final String CHECKSTYLE_XML_PATH_KEY = CheckStyleTaskConfigurator.CHECKSTYLE_PATH;

    public static final String CHECKSTYLE_TOTAL_VIOLATION_DELTA = "CHECKSTYLE_TOTAL_VIOLATION_DELTA";
    public static final String CHECKSTYLE_ERROR_VIOLATION_DELTA = "CHECKSTYLE_ERROR_VIOLATION_DELTA";
    public static final String CHECKSTYLE_WARNING_VIOLATION_DELTA = "CHECKSTYLE_WARNING_VIOLATION_DELTA";
    public static final String CHECKSTYLE_INFO_VIOLATION_DELTA = "CHECKSTYLE_INFO_VIOLATION_DELTA";

    public static final String CHECKSTYLE_TOTAL_VIOLATIONS = "CHECKSTYLE_TOTAL_VIOLATIONS";
    public static final String CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS = "CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS";
    public static final String CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS = "CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS";
    public static final String CHECKSTYLE_INFO_PRIORITY_VIOLATIONS = "CHECKSTYLE_INFO_PRIORITY_VIOLATIONS";

    public static final String CHECKSTYLE_TOP_VIOLATIONS = "CHECKSTYLE_TOP_VIOLATIONS";

    public static final String BUILD_NUMBER = "buildNumber";
    public static final String BUILD_KEY = "buildKey";

    public static final String CHECKSTYLE_ENABLE_INTEGRATION = "custom.checkstyle.enable.integration";
    public static final String CHECKSTYLE_JSON_ARTIFACT_LABEL = "Checkstyle JSON Report (System)";
    public static final String CHECKSTYLE_JSON_ARTIFACT_LOCATION = "checkstyle-json";
    public static final String CHECKSTYLE_JSON_ARTIFACT_FILE_NAME = "checkstyle.json";
}