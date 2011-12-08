/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.actions;

import java.util.Collections;
import java.util.List;

import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

import com.atlassian.bamboo.build.FilterController;
import com.atlassian.bamboo.reports.collector.ReportCollector;
import com.atlassian.bamboo.ww2.actions.BuildActionSupport;
import com.atlassian.bamboo.ww2.aware.ResultsListAware;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginManager;

/************************************************************************************************************
 * ViewCheckStyleSummary.
 * 
 * @author Stephan Paulicke
 */
public class ViewCheckStyleSummary extends BuildActionSupport implements ResultsListAware {
  // =======================================================================================================
  // === ATTRIBUTES
  // =======================================================================================================
  private static final long serialVersionUID = -4957311081781987092L;
  private static final String CHECKSTYLE_VIOLATIONS_REPORT_KEY = "com.atlassian.bamboo.plugins.checkstyle:checkstyleViolations";
  private static final String CHECKSTYLE_VIOLATIONS_DELTA_REPORT_KEY = "com.atlassian.bamboo.plugins.checkstyle:authorCheckStyleDelta";
  
  private PluginManager pluginManager;
  private List resultsList;
  private XYDataset dataset;
  private FilterController filterController;
  private String reportKey = CHECKSTYLE_VIOLATIONS_REPORT_KEY;
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * doViewViolationSummary().
   * 
   * @return
   * @throws Exception
   */
  public String doViewViolationSummary() throws Exception {
    setReportKey(CHECKSTYLE_VIOLATIONS_REPORT_KEY);
    return run();
  }
  
  /********************************************************************************************************
   * doViewCheckStyleDeltaByAuthor().
   * 
   * @return
   * @throws Exception
   */
  public String doViewCheckStyleDeltaByAuthor() throws Exception {
    setReportKey(CHECKSTYLE_VIOLATIONS_DELTA_REPORT_KEY);
    return run();
  }
  
  /********************************************************************************************************
   * execute().
   * 
   * @return
   */
  public String execute() throws Exception {
    return run();
  }
  
  /********************************************************************************************************
   * run().
   * 
   * @return
   */
  private String run() {
    if ((resultsList != null) && !resultsList.isEmpty()) {
      ModuleDescriptor descriptor = pluginManager.getPluginModule(getReportKey());
      if (descriptor != null) {
        ReportCollector collector = (ReportCollector) descriptor.getModule();
        collector.setResultsList(getResultsList());
        collector.setParams(Collections.EMPTY_MAP);
        dataset = (TimeTableXYDataset) collector.getDataset();
      }
    }
    return SUCCESS;
  }
  
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  public XYDataset getDataset() {
    return dataset;
  }
  
  public void setDataset(XYDataset dataset) {
    this.dataset = dataset;
  }
  
  public FilterController getFilterController() {
    return filterController;
  }
  
  public void setFilterController(FilterController filterController) {
    this.filterController = filterController;
  }
  
  public PluginManager getPluginManager() {
    return pluginManager;
  }
  
  public void setPluginManager(PluginManager pluginManager) {
    this.pluginManager = pluginManager;
  }
  
  public List getResultsList() {
    return resultsList;
  }
  
  public void setResultsList(List resultsList) {
    this.resultsList = resultsList;
  }
  
  public String getReportKey() {
    return reportKey;
  }
  
  public void setReportKey(String reportKey) {
    this.reportKey = reportKey;
  }
}