/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.atlassian.bamboo.index.CustomPostBuildIndexWriter;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.util.NumberUtils;

/************************************************************************************************************
 * CheckStylePostBuildIndexWriter.
 * 
 * @author Stephan Paulicke
 */
public class CheckStylePostBuildIndexWriter implements CustomPostBuildIndexWriter {
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * updateIndexDocument().
   * 
   * @param document
   * @param resultsSummary
   */
  public void updateIndexDocument(Document document, BuildResultsSummary resultsSummary) {
    if (resultsSummary != null) {
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATIONS, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_VIOLATION_DELTA, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_VIOLATION_DELTA, document, resultsSummary);
      addStuffToIndex(ICheckStyleBuildProcessor.CHECKSTYLE_INFO_VIOLATION_DELTA, document, resultsSummary);
    }
  }
  
  /********************************************************************************************************
   * addStuffToIndex().
   * 
   * @param key
   * @param document
   * @param resultsSummary
   */
  private void addStuffToIndex(String key, Document document, BuildResultsSummary resultsSummary) {
    Map map = resultsSummary.getCustomBuildData();
    if (map.containsKey(key)) {
      Object o = map.get(key);
      if ((o != null) && (o instanceof String)) {
        String violations = (String) o;
        long violationsLong = Long.parseLong(violations);
        Field field = new Field(key, NumberUtils.padWithZeroes(violationsLong, 12), Field.Store.YES, Field.Index.NOT_ANALYZED);
        document.add(field);
      }
    }
  }
}