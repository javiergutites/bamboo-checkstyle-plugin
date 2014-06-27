/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import com.atlassian.bamboo.index.CustomIndexReader;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.util.NumberUtils;
import org.apache.lucene.document.Document;

/************************************************************************************************************
 * CheckStyleIndexReader.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleIndexReader implements CustomIndexReader {
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * extractFromDocument().
   * 
   * @param document
   * @param buildResultsSummary
   */
  public void extractFromDocument(Document document, BuildResultsSummary buildResultsSummary) {
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_TOTAL_VIOLATIONS, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_TOTAL_VIOLATION_DELTA, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_ERROR_VIOLATION_DELTA, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_WARNING_VIOLATION_DELTA, document, buildResultsSummary);
    addToBuildSummary(CheckStyleBambooConstants.CHECKSTYLE_INFO_VIOLATION_DELTA, document, buildResultsSummary);
  }
  
  /********************************************************************************************************
   * addToBuildSummary().
   * 
   * @param document
   * @param key
   * @param buildResultsSummary
   */
  private void addToBuildSummary(String key, Document document, BuildResultsSummary buildResultsSummary) {
    String s = document.get(key);
    if ((s != null) && (s.length() > 0)) {
      double violations = NumberUtils.stringToDouble(s);
      buildResultsSummary.getCustomBuildData().put(key, Double.toString(violations));
    }
  }
}