/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.collators;

import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.charts.collater.TimePeriodCollater;
import com.atlassian.bamboo.charts.timeperiod.AbstractTimePeriodCollater;
import com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginConstants;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultStatisticsProvider;

import java.util.Map;

/************************************************************************************************************
 * CheckStyleLineRateViolationCollator.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleLineRateViolationCollator extends AbstractTimePeriodCollater implements TimePeriodCollater {
  // =======================================================================================================
  // === ATTRIBUTES
  // =======================================================================================================
  private int count;
  private double violations;
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * addResult().
   * 
   * @param result
   */
  public void addResult(ResultStatisticsProvider result) {
    if (BuildState.SUCCESS.equals(result.getBuildState()) && (result != null) && (result instanceof BuildResultsSummary)) {
      BuildResultsSummary resultSumm = (BuildResultsSummary) result;
      Map map = resultSumm.getCustomBuildData();
      if (map != null) {
        String violationsStr;
        double violationsDbl = 0;
        boolean add = false;
        if (getSeriesName().equals("total") && map.containsKey(CheckstylePluginConstants.CHECKSTYLE_TOTAL_VIOLATIONS)) {
          violationsStr = (String) map.get(CheckstylePluginConstants.CHECKSTYLE_TOTAL_VIOLATIONS);
          violationsDbl = Double.parseDouble(violationsStr);
          add = true;
        }
        if (getSeriesName().equals("error") && map.containsKey(CheckstylePluginConstants.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS)) {
          violationsStr = (String) map.get(CheckstylePluginConstants.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS);
          violationsDbl = Double.parseDouble(violationsStr);
          add = true;
        }
        if (getSeriesName().equals("warning") && map.containsKey(CheckstylePluginConstants.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS)) {
          violationsStr = (String) map.get(CheckstylePluginConstants.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS);
          violationsDbl = Double.parseDouble(violationsStr);
          add = true;
        }
        if (getSeriesName().equals("info") && map.containsKey(CheckstylePluginConstants.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS)) {
          violationsStr = (String) map.get(CheckstylePluginConstants.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS);
          violationsDbl = Double.parseDouble(violationsStr);
          add = true;
        }
        if (add) {
          violations = (violations + violationsDbl);
          count++;
        }
      }
    }
  }
  
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  public double getValue() {
    if (count == 0) {
      return 0.0D;
    } else {
      return (violations / (double) count);
    }
  }
}