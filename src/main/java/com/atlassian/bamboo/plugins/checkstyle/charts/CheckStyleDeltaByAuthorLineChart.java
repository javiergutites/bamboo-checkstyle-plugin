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
 * CheckStyleDeltaByAuthorLineChart.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleDeltaByAuthorLineChart extends BambooReportLineChart implements XYToolTipGenerator {
  // =======================================================================================================
  // === CONSTRUCTOR
  // =======================================================================================================
  
  /********************************************************************************************************
   * CheckStyleDeltaByAuthorLineChart.
   */
  public CheckStyleDeltaByAuthorLineChart() {
    setyAxisLabel("CheckStyle Violation Delta");
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
    double checkstyleDelta = dataset.getYValue(series, item);
    String authorName = (String) dataset.getSeriesKey(series);
    TimePeriod timePeriod = dataset.getTimePeriod(item);
    return NumberUtils.round(checkstyleDelta, 1) + " CheckStyle violation delta on " + timePeriod + " for " + authorName;
  }
}
