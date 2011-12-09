/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.atlassian.bamboo.plugins.checkstyle.ICheckStyleBuildProcessor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.bamboo.build.ViewBuildResults;
import com.atlassian.bamboo.plugins.checkstyle.CheckStyleViolationInformation;
import com.atlassian.bamboo.results.BuildResults;

/************************************************************************************************************
 * ViewCheckStyleBuildResults.
 * 
 * @author Stephan Paulicke
 */
public class ViewCheckStyleBuildResults extends ViewBuildResults {
  // =======================================================================================================
  // === ATTRIBUTES
  // =======================================================================================================
  private static final long serialVersionUID = 7501144681146083536L;
  private List<CheckStyleViolationInformation> topViolations = new ArrayList<CheckStyleViolationInformation>();
  private static final Logger log = Logger.getLogger(ViewCheckStyleBuildResults.class);
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * execute().
   * 
   * @return
   * @throws Exception
   */
  @Override
  public String execute() throws Exception {
    String superResult = super.doExecute();
    if (ERROR.equals(superResult)) {
      return ERROR;
    }
    populateTopViolations(getBuildResults());
    return superResult;
  }
  
  /********************************************************************************************************
   * populateTopViolations().
   * 
   * @param buildResults
   */
  private void populateTopViolations(BuildResults buildResults) {
    String csv = (String) buildResults.getBuildResultsSummary().getCustomBuildData().get(ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS);
    if ((csv != null) && !StringUtils.isEmpty(csv)) {
      BufferedReader reader = new BufferedReader(new StringReader(csv));
      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          StringTokenizer tokenizer = new StringTokenizer(line, ",");
          String fileName = tokenizer.nextToken();
          Integer numberOfViolations = Integer.parseInt(tokenizer.nextToken());
          topViolations.add(new CheckStyleViolationInformation(fileName, numberOfViolations));
        }
      } catch (NumberFormatException e) {
        log.error(e);
      } catch (IOException e) {
        log.error(e);
      }
    }
  }
  
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  public List<CheckStyleViolationInformation> getTopViolations() {
    return topViolations;
  }
}