/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.charts;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

import com.atlassian.bamboo.reports.charts.BambooReportLineChart;
import com.atlassian.bamboo.util.NumberUtils;

/************************************************************************************************************
 * ViolationSummaryLineChart.
 * 
 * @author Stephan Paulicke
 */
public class ViolationSummaryLineChart extends BambooReportLineChart implements XYToolTipGenerator {
  // =======================================================================================================
  // === CONSTRUCTOR
  // =======================================================================================================
  
  /********************************************************************************************************
   * ViolationSummaryLineChart.
   */
  public ViolationSummaryLineChart() {
    setyAxisLabel("Number of Style Violations");
  }
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * generateToolTip().
   * 
   * @param xyDataset
   * @param series
   * @param item
   * @return
   */
  public String generateToolTip(XYDataset xyDataset, int series, int item) {
    TimeTableXYDataset dataset = (TimeTableXYDataset) xyDataset;
    double violations = dataset.getYValue(series, item);
    String buildKey = (String) dataset.getSeriesKey(series);
    TimePeriod timePeriod = dataset.getTimePeriod(item);
    return "Build in " + timePeriod + " contained " + NumberUtils.round(violations, 1) + " style violations in build " + buildKey;
  }
}
