package it.com.atlassian.bamboo.plugins.checkstyle;

import java.net.URL;

import junit.framework.Assert;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * 
 * 
 */
public class TestLogin
    extends SeleneseTestCase
{
    public TestLogin()
    {
    	setCaptureScreenShotOnFailure( true );
    }

    public void setUp()
        throws Exception
    {
        setUp( "http://localhost:1990/", "*firefox" );
    }

    public void testCreateNewProject()
        throws Exception
    {
        CommonSeleneseScenarios.login( selenium );
        CommonSeleneseScenarios.checkStylePluginIsPresent( selenium );
        CommonSeleneseScenarios.maven2BuilderIsPresent( selenium );

        deleteIfExist("TESTCS", "SIMPLE");

        createNewProject();

        pause( 7000 );

        waitBuildSuccessful("TESTCS-SIMPLE");

        // Assert Action checkstyle is present
        selenium.open( "/bamboo/build/viewCheckStyleSummary.action?buildKey=TESTCS-SIMPLE" );
        selenium.click( "CheckStyle:TESTCS-SIMPLE" );
        selenium.waitForPageToLoad( "30000" );
        verifyTrue( selenium.isTextPresent( "CheckStyle Violations" ) );

        // View build result
        selenium.open( "/bamboo/build/viewCheckStyleResult.action?buildKey=TESTCS-SIMPLE&buildNumber=1" );
        selenium.waitForPageToLoad( "30000" );
        assertTrue( selenium.isTextPresent( "Checkstyle Summary" ) );
        assertTrue( selenium.isTextPresent( "TOTAL" ) );
        assertTrue( selenium.isTextPresent( "11" ) );
        assertTrue( selenium.isTextPresent( "App.java" ) );
        assertTrue( selenium.isTextPresent( "package.html" ) );

        // relaunch a build
        selenium.open( "/bamboo/build/admin/triggerManualBuild.action?buildKey=TESTCS-SIMPLE" );
        selenium.waitForPageToLoad( "30000" );

        waitBuildSuccessful("TESTCS-SIMPLE");

        // Assert tab sheet checkstyle is present
        selenium.open( "/bamboo/build/viewCheckStyleResult.action?buildKey=TESTCS-SIMPLE&buildNumber=2" );
        selenium.waitForPageToLoad( "30000" );

        // Assert delta is present
        Assert.assertEquals( "0", selenium.getText( "//div[@id='fb_summary']/div/div[1]/div/table/tbody/tr[2]/td[2]" ) );
        Assert.assertEquals( "0", selenium.getText( "//div[@id='fb_summary']/div/div[1]/div/table/tbody/tr[3]/td[2]" ) );
        Assert.assertEquals( "0", selenium.getText( "//div[@id='fb_summary']/div/div[1]/div/table/tbody/tr[4]/td[2]" ) );
        Assert.assertEquals( "0", selenium.getText( "//div[@id='fb_summary']/div/div[1]/div/table/tbody/tr[5]/td[2]" ) );
    }

    /**
     * http://developer.atlassian.com/jira/browse/BCHKSTYL-9
     */
    public void testSubMenuCheckstyle()
        throws Exception
    {
        CommonSeleneseScenarios.login( selenium );

        // Assert Action checkstyle is present
        selenium.open( "/bamboo/browse/TESTCS-SIMPLE" );
        selenium.waitForPageToLoad( "30000" );

        assertTrue( selenium.isTextPresent( "CheckStyle" ) );
        assertTrue( selenium.isElementPresent( "CheckStyle:TESTCS-SIMPLE" ) );

        selenium.open( "/bamboo/build/admin/edit/editBuildConfiguration.action?buildKey=TESTCS-SIMPLE" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "//li[3]/a/em" );

        // Unselect checkstyle plugin
        selenium.click( "updateBuildBuilder_custom_checkstyle_exists" );

        // Save the configuration
        selenium.click( "//form[@id='updateBuildBuilder']/div/div[2]/div/input[2]" );
        selenium.waitForPageToLoad( "30000" );

        assertFalse( selenium.isTextPresent( "CheckStyle" ) );
        assertFalse( selenium.isElementPresent( "CheckStyle:TESTCS-SIMPLE" ) );
    }

    /**
     * http://developer.atlassian.com/jira/browse/BCHKSTYL-6
     */
    public void testCheckstyleResultWithHref()
        throws Exception
    {
        CommonSeleneseScenarios.login( selenium );

        deleteIfExist("M2PLUGINS", "MCHECKSTYLE");
        createMavenCheckstyleProject();

        pause( 7000 );

        waitBuildSuccessful("M2PLUGINS-MCHECKSTYLE");
        
        // Assert Action checkstyle is present
        selenium.open( "/bamboo/build/viewCheckStyleSummary.action?buildKey=M2PLUGINS-MCHECKSTYLE" );
        selenium.waitForPageToLoad( "30000" );
        assertTrue( selenium.isTextPresent( "CheckStyle Violations" ) );
        
        //verify the balise href
        selenium.open( "/bamboo/build/viewCheckStyleResult.action?buildKey=M2PLUGINS-MCHECKSTYLE&buildNumber=1");
        selenium.waitForPageToLoad( "30000" );
        
        //href is generated
        selenium.click("//div[@id='fb_summary']/ul/li[2]/a/em");
        assertTrue( selenium.isTextPresent( "org.apache.maven.plugin.checkstyle.CheckstyleReport.java" ) );
        assertEquals("org.apache.maven.plugin.checkstyle.CheckstyleReport.java", selenium.getText("link=org.apache.maven.plugin.checkstyle.CheckstyleReport.java"));

        //assert URL generated
        String htmlSource = selenium.getHtmlSource();
        assertTrue(htmlSource.contains( "http://maven.apache.org/plugins/maven-checkstyle-plugin/checkstyle.html#org.apache.maven.plugin.checkstyle.CheckstyleReport.java" ));
    }
    
    /**
     * 
     */
    private void waitBuildSuccessful(String key)
    {
        String url = "/bamboo/browse/" + key;
        selenium.open( url );
        selenium.waitForPageToLoad( "30000" );
        int triesNumber = 10;
        int i = 0;
        while ( !selenium.isTextPresent( "was successful" ) && i++ < triesNumber )
        {
            pause( 1000 );
            selenium.open( url );
            selenium.waitForPageToLoad( "30000" );
        }
    }

    private void deleteIfExist(String projectKey, String planKey)
    {
        selenium.open( "/bamboo/admin/administer.action" );
        selenium.click( "deleteBuilds" );
        selenium.waitForPageToLoad( "30000" );

        if ( selenium.isTextPresent( projectKey ) && selenium.isTextPresent( planKey ) )
        {
            selenium.click( "checkbox_" + projectKey);
            selenium.click( "save" );
            selenium.waitForPageToLoad( "10000" );
            selenium.click( "save" );
        }
    }
    
    public void createMavenCheckstyleProject()
    {
        selenium.open( "/bamboo/start.action" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "create" );
        selenium.waitForPageToLoad( "30000" );

        selenium.type( "createBuild_projectName", "Maven Plugins" );
        selenium.type( "createBuild_projectKey", "M2PLUGINS" );
        selenium.type( "createBuild_buildName", "Maven Checkstyle" );
        selenium.type( "createBuild_buildKey", "MCHECKSTYLE" );
        selenium.click( "save" );
        selenium.waitForPageToLoad( "30000" );

        selenium.select( "createBuildRepository_selectedRepository", "label=Subversion" );
        selenium.type( "repository.svn.repositoryUrl", 
                       "http://svn.apache.org/repos/asf/maven/plugins/tags/maven-checkstyle-plugin-2.2" );
        selenium.click( "save" );
        selenium.waitForPageToLoad( "30000" );

        selenium.select( "createBuildBuilder_selectedBuilderKey", "label=Maven 2" );
        selenium.type( "createBuildBuilder_builder_mvn2_goal", "checkstyle:checkstyle" );
        selenium.click( "createBuildBuilder_custom_checkstyle_exists" );
        selenium.type( "custom.checkstyle.path", "**/target/checkstyle-result.xml" );
        selenium.type( "custom.checkstyle.site.url", "http://maven.apache.org/plugins/maven-checkstyle-plugin/" );
        selenium.click( "save" );
        selenium.waitForPageToLoad( "30000" );

        selenium.click( "nextButton" );
        selenium.waitForPageToLoad( "30000" );

        selenium.click( "nextButton" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "nextButton" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "next" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "//form[@id='permissions']/div/div[4]/div/input[3]" );
        selenium.waitForPageToLoad( "30000" );
    }
    
    public void createNewProject()
    {
        String svnRepoURl = getSvnRepoURL();

        selenium.open( "/bamboo/start.action" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "create" );
        selenium.waitForPageToLoad( "30000" );

        selenium.type( "createBuild_projectName", "TESTCS" );
        selenium.type( "createBuild_projectKey", "TESTCS" );
        selenium.type( "createBuild_buildName", "SIMPLE" );
        selenium.type( "createBuild_buildKey", "SIMPLE" );
        selenium.click( "save" );
        selenium.waitForPageToLoad( "30000" );

        selenium.select( "createBuildRepository_selectedRepository", "label=Subversion" );
        selenium.type( "repository.svn.repositoryUrl", svnRepoURl );
        selenium.click( "save" );
        selenium.waitForPageToLoad( "30000" );

        selenium.select( "createBuildBuilder_selectedBuilderKey", "label=Maven 2" );
        selenium.type( "createBuildBuilder_builder_mvn2_goal", "site" );
        selenium.click( "createBuildBuilder_custom_checkstyle_exists" );
        selenium.type( "custom.checkstyle.path", "**/target/checkstyle-result.xml" );
        selenium.click( "save" );
        selenium.waitForPageToLoad( "30000" );

        selenium.click( "nextButton" );
        selenium.waitForPageToLoad( "30000" );

        selenium.click( "nextButton" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "nextButton" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "next" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "//form[@id='permissions']/div/div[4]/div/input[3]" );
        selenium.waitForPageToLoad( "30000" );
    }

    private String getSvnRepoURL()
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "svn-repo" );
        return url.getProtocol() + "://" + url.getFile() + "/simple-project/trunk";
    }
}