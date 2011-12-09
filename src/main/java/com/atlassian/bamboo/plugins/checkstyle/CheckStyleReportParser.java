/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.atlassian.bamboo.plugins.checkstyle.CheckStyleViolationInformation;

/************************************************************************************************************
 * CheckStyleReportParser.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleReportParser {
  // =======================================================================================================
  // === ATTRIBUTES
  // =======================================================================================================
  private long totalViolations;
  private long errorPriorityViolations;
  private long warningPriorityViolations;
  private long infoPriorityViolations;
  
  private Hashtable<String, Integer> violationsPerFile;
  
  // =======================================================================================================
  // === METHODS
  // =======================================================================================================
  
  /********************************************************************************************************
   * parse().
   * 
   * @param stream
   */
  public void parse(InputStream stream) throws DocumentException {
    SAXReader reader = new SAXReader();
    Document document = reader.read(stream);
    parse(document);
  }
  
  /********************************************************************************************************
   * parse().
   * 
   * @param file
   * @throws MalformedURLException
   * @throws DocumentException
   */
  public void parse(File file) throws MalformedURLException, DocumentException {
    SAXReader reader = new SAXReader();
    Document document = reader.read(file);
    parse(document);
  }
  
  /********************************************************************************************************
   * parse().
   * 
   * @param document
   */
  private void parse(Document document) {
    violationsPerFile = new Hashtable<String, Integer>();
    errorPriorityViolations = 0;
    warningPriorityViolations = 0;
    infoPriorityViolations = 0;
    List<Node> files = document.selectNodes("//checkstyle/file");
    for (Iterator<Node> iterator = files.iterator(); iterator.hasNext();) {
      Node fileNode = iterator.next();
      List<Node> xErrors = fileNode.selectNodes("error");
      for (Iterator<Node> iterator2 = xErrors.iterator(); iterator2.hasNext();) {
        Node errorNode = iterator2.next();
        String name = fileNode.valueOf("@name");
        if (!violationsPerFile.containsKey(name)) {
          violationsPerFile.put(name, new Integer(0));
        }
        violationsPerFile.put(name, new Integer(violationsPerFile.get(name).intValue() + 1));
        
        String severity = errorNode.valueOf("@severity");
        if (severity != null) {
          severity = severity.toUpperCase();
          if (severity.equalsIgnoreCase("error")) {
            errorPriorityViolations++;
          } else if (severity.equalsIgnoreCase("warning")) {
            warningPriorityViolations++;
          } else if (severity.equalsIgnoreCase("info")) {
            infoPriorityViolations++;
          }
        }
      }
    }
    totalViolations = (errorPriorityViolations + warningPriorityViolations + infoPriorityViolations);
  }
  
  /********************************************************************************************************
   * convertTopViolationsToCsv().
   * 
   * @param file
   * @return
   * @throws FileNotFoundException
   */
  public String convertTopViolationsToCsv() throws FileNotFoundException {
      return CsvHelper.convertTopViolationsToCsv(violationsPerFile);
  }

  // =======================================================================================================
  // === PROPERTIES
  // =======================================================================================================
  
  public long getTotalViolations() {
    return totalViolations;
  }
  
  public long getErrorPriorityViolations() {
    return errorPriorityViolations;
  }
  
  public long getInfoPriorityViolations() {
    return infoPriorityViolations;
  }
  
  public long getWarningPriorityViolations() {
    return warningPriorityViolations;
  }
}