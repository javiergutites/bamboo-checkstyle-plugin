/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import com.atlassian.bamboo.charts.collater.TimePeriodCollater;
import com.atlassian.bamboo.plugins.checkstyle.charts.AbstractMultiSeriesTimePeriodCollector;
import com.atlassian.bamboo.plugins.checkstyle.collators.CheckStyleLineRateViolationCollator;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;

/************************************************************************************************************
 * CheckStyleViolationCollector.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleViolationCollector extends AbstractMultiSeriesTimePeriodCollector {
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  protected String[] getSeriesKeys(BuildResultsSummary summary) {
    return new String[] { "total", "error", "warning", "info" };
  }
  
  @Override
  protected TimePeriodCollater getCollater() {
    return new CheckStyleLineRateViolationCollator();
  }
}
