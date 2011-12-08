package it.com.atlassian.bamboo.plugins.checkstyle;

import junit.framework.Assert;

import com.thoughtworks.selenium.Selenium;

public class CommonSeleneseScenarios {

  public static void login(Selenium selenium) throws Exception {
    selenium.open("/bamboo/userlogin.action");
    selenium.waitForPageToLoad("30000");
    selenium.type("loginForm_os_username", "admin");
    selenium.type("loginForm_os_password", "admin");
    selenium.click("os_cookie_id");
    selenium.click("//form[@id='loginForm']/div/div[6]/div/input[2]");
    selenium.waitForPageToLoad("30000");
    
    Assert.assertTrue(selenium.isTextPresent("Admin"));
  }
  
  public static void checkStylePluginIsPresent(Selenium selenium) throws Exception {
    selenium.open("/bamboo/admin/administer.action");
    selenium.waitForPageToLoad("30000");
    
    selenium.open("/bamboo/admin/systemInfo.action");
    selenium.waitForPageToLoad("30000");
    
    Assert.assertTrue(selenium.isTextPresent("Bamboo CheckStyle Plugin"));
  }
  
  public static void maven2BuilderIsPresent(Selenium selenium) throws Exception {
      selenium.open("/bamboo/admin/agent/viewBuilders.action");
      selenium.waitForPageToLoad("30000");
      
      //Delete old Builder
      selenium.click("link=Delete");
      Assert.assertNotNull(selenium.getConfirmation());

      selenium.click("link=Delete");
      Assert.assertNotNull(selenium.getConfirmation());
      
      selenium.click("link=Delete");
      Assert.assertNotNull(selenium.getConfirmation());
      
      //Delete All JDK
      selenium.open("/bamboo/admin/agent/viewJdks.action");
      selenium.waitForPageToLoad("30000");
      selenium.click("link=Delete");
      Assert.assertNotNull(selenium.getConfirmation());

      selenium.click("link=Delete");
      Assert.assertNotNull(selenium.getConfirmation());
      
      selenium.click("link=Delete");
      Assert.assertNotNull(selenium.getConfirmation());
      
      //Automatic detection
      selenium.click("serverCapabilities");
      selenium.waitForPageToLoad("30000");
      selenium.click("link=Automatically detect server capabilities");
      selenium.waitForPageToLoad("30000");
      
      //Verify Maven2 & JDK
      Assert.assertTrue(selenium.isTextPresent("Maven 2 (Maven 2.0)"));
      Assert.assertTrue(selenium.isTextPresent("JDK"));
  }
}
