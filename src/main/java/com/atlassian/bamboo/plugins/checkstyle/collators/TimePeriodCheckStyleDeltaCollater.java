/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.collators;

import java.util.Map;

import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.charts.collater.TimePeriodCollater;
import com.atlassian.bamboo.charts.timeperiod.AbstractTimePeriodCollater;
import com.atlassian.bamboo.plugins.checkstyle.CheckStyleBuildProcessor;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultStatisticsProvider;

/************************************************************************************************************
 * TimePeriodCheckStyleDeltaCollater.
 * 
 * @author Stephan Paulicke
 */
public class TimePeriodCheckStyleDeltaCollater extends AbstractTimePeriodCollater implements TimePeriodCollater {
  // =======================================================================================================
  // === ATTRIBUTES
  // =======================================================================================================
  private double totalDelta;
  
  // =======================================================================================================
  // === CONSTRUCTOR
  // =======================================================================================================
  
  /********************************************************************************************************
   * TimePeriodCheckStyleCollator.
   */
  public TimePeriodCheckStyleDeltaCollater() {
    totalDelta = 0.0;
  }
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * addResult().
   * 
   * @param resultStatisticsProvider
   */
  public void addResult(ResultStatisticsProvider resultStatisticsProvider) {
    if (BuildState.SUCCESS.equals(resultStatisticsProvider.getBuildState()) && resultStatisticsProvider instanceof BuildResultsSummary) {
      BuildResultsSummary results = (BuildResultsSummary) resultStatisticsProvider;
      int authorCount = 0;
      if (results.getChangedByAuthors() != null && results.getChangedByAuthors().length() > 0) {
        authorCount = parseAuthors(results.getChangedByAuthors()).length;
      }
      if (authorCount < 1) {
        authorCount = 1;
      }
      Map buildData = results.getCustomBuildData();
      if ((buildData != null) && buildData.containsKey(CheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA)) {
        String violationsStr = (String) buildData.get(CheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA);
        totalDelta += Double.parseDouble(violationsStr) / (double) authorCount;
      }
    }
  }
  
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  public double getValue() {
    return totalDelta;
  }
  
  public static String[] parseAuthors(String authors) {
    return authors.split(", +");
  }
}
