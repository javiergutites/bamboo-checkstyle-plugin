/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.charts;

import com.atlassian.bamboo.charts.collater.TimePeriodCollater;
import com.atlassian.bamboo.plugins.checkstyle.collators.TimePeriodCheckStyleDeltaCollater;
import com.atlassian.bamboo.resultsummary.ResultsSummary;

/************************************************************************************************************
 * ViewCheckStyleSummary.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleDeltaByAuthorCollector extends AbstractMultiSeriesTimePeriodCollector {
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  protected String[] getSeriesKeys(ResultsSummary summary) {
    return TimePeriodCheckStyleDeltaCollater.authorsToStrings(summary.getUniqueAuthors());
  }
  
  protected TimePeriodCollater getCollater() {
    return new TimePeriodCheckStyleDeltaCollater();
  }
}
