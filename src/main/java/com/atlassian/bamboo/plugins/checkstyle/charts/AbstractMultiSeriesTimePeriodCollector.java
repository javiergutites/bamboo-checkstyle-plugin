/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.charts;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;

import com.atlassian.bamboo.charts.collater.TimePeriodCollater;
import com.atlassian.bamboo.reports.collector.AbstractTimePeriodCollector;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;

/************************************************************************************************************
 * AbstractMultiSeriesTimePeriodCollector.
 */
public abstract class AbstractMultiSeriesTimePeriodCollector extends AbstractTimePeriodCollector {
  // =======================================================================================================
  // === CONSTRUCTOR
  // =======================================================================================================
  
  /********************************************************************************************************
   * AbstractMultiSeriesTimePeriodCollector.
   */
  public AbstractMultiSeriesTimePeriodCollector() {
    // --- nothing to do here
  }
  
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  @SuppressWarnings("unchecked")
  public Dataset getDataset() {
    Map<String, TimePeriodCollater> seriesToPeriodMap = new HashMap<String, TimePeriodCollater>();
    TimeTableXYDataset dataset = new TimeTableXYDataset();
    for (Iterator<BuildResultsSummary> iterator = getResultsList().iterator(); iterator.hasNext();) {
      BuildResultsSummary summary = iterator.next();
      String[] seriesKeys = getSeriesKeys(summary);
      if (seriesKeys != null) {
        for (int i = 0; (i < seriesKeys.length); i++) {
          String key = seriesKeys[i];
          Date buildDate = summary.getBuildDate();
          TimePeriodCollater collaterForSeries = (TimePeriodCollater) seriesToPeriodMap.get(key);
          if (collaterForSeries == null) {
            collaterForSeries = createCollater(getPeriod(buildDate, getPeriodRange()), key);
            collaterForSeries.addResult(summary);
            seriesToPeriodMap.put(key, collaterForSeries);
          } else if (isInPeriod(collaterForSeries.getPeriod(), buildDate)) {
            collaterForSeries.addResult(summary);
          } else {
            writeCollaterToDataSet(dataset, collaterForSeries);
            RegularTimePeriod nextPeriod = collaterForSeries.getPeriod().next();
            while (!isInPeriod(nextPeriod, buildDate)) {
              nextPeriod = nextPeriod.next();
            }
            collaterForSeries = createCollater(nextPeriod, key);
            collaterForSeries.addResult(summary);
            seriesToPeriodMap.put(key, collaterForSeries);
          }
        }
      }
    }
    Collection<TimePeriodCollater> finalPeriods = seriesToPeriodMap.values();
    TimePeriodCollater timePeriodCollater;
    for (Iterator<TimePeriodCollater> iterator = finalPeriods.iterator(); iterator.hasNext(); writeCollaterToDataSet(dataset, timePeriodCollater)) {
      timePeriodCollater = iterator.next();
    }
    return dataset;
  }
  
  protected abstract String[] getSeriesKeys(BuildResultsSummary summary);
}
