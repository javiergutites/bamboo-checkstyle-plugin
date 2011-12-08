package com.atlassian.bamboo.plugins.checkstyle;

/**
 * Interface for share the common constants
 * 
 * @author lauvigne
 */
public interface ICheckStyleBuildProcessor {

  public static final String CHECKSTYLE_PATH = "custom.checkstyle.path";
  public static final String CHECKSTYLE_EXISTS = "custom.checkstyle.exists";
  public static final String CHECKSTYLE_SITE_URL = "custom.checkstyle.site.url";
  public static final String CHECKSTYLE_ERROR_PRIORITY_THRESHOLD = "custom.checkstyle.threshold.error";
  public static final String CHECKSTYLE_WARNING_PRIORITY_THRESHOLD = "custom.checkstyle.threshold.warning";

  public static final String CHECKSTYLE_XML_PATH_KEY = CHECKSTYLE_PATH;

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
}