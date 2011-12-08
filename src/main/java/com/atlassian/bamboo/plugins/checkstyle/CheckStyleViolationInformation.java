/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

/************************************************************************************************************
 * CheckStyleViolationInformation.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleViolationInformation implements Comparable<Object> {
  // =======================================================================================================
  // === ATTRIBUTES
  // =======================================================================================================
  private String fileName;
  private Integer numberOfViolations;
  
  // =======================================================================================================
  // === CONSTRUCTOR
  // =======================================================================================================
  
  /********************************************************************************************************
   * CheckStyleViolationInformation.
   * 
   * @param fileName
   * @param numberOfViolations
   */
  public CheckStyleViolationInformation(String fileName, Integer numberOfViolations) {
    this.fileName = fileName;
    this.numberOfViolations = numberOfViolations;
  }
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * compareTo().
   * 
   * @param object
   * @return
   */
  public int compareTo(Object object) {
    if (object instanceof CheckStyleViolationInformation) {
      return (((CheckStyleViolationInformation) object).getNumberOfViolations() > getNumberOfViolations()) ? 1 : 0;
    }
    return 1;
  }
  
  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  public String getFileName() {
    if (fileName != null) {
      return fileName.replaceAll("\\\\", "/");
    } else {
      return null;
    }
  }
  
  public void setClassName(String fileName) {
    this.fileName = fileName;
  }
  
  public Integer getNumberOfViolations() {
    return numberOfViolations;
  }
  
  public void setNumberOfViolations(Integer numberOfViolations) {
    this.numberOfViolations = numberOfViolations;
  }
}