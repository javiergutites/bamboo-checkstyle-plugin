/**
 * 
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author lauvigne
 */
public class CheckStyleFileVisitorTest
    extends TestCase
{
    /** The visitor to test */
    private CheckStyleFileVisitor visitor;

    /** The url of sample checkstyle result */
    private URL url;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        url = Thread.currentThread().getContextClassLoader().getResource( "checkstyle-result/testSimple/checkstyle-result.xml" );
        assertNotNull( url );

        visitor = new CheckStyleFileVisitor( new File( "." ), new HashMap<String, String>() );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleFileVisitor#runParse(java.io.File)}.
     */
    public final void testRunParseSimple()
    {
        visitor.runParse( new File( url.getFile() ) );
        assertViolations( "764", "185", "14", "963", ICvsHelperConst.SAMPLE_TOP_VIOLATIONS );
    }

    /**
     * 
     */
    protected void assertViolations( String errorViolations, String warningViolations, String infoViolations, String totalViolations, String topViolations )
    {
        assertEquals( 5, visitor.getResults().size() );
        assertEquals( errorViolations, visitor.getResults().get( ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS ) );
        assertEquals( infoViolations, visitor.getResults().get( ICheckStyleBuildProcessor.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS ) );
        assertEquals( warningViolations, visitor.getResults().get( ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS ) );
        assertEquals( totalViolations, visitor.getResults().get( ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATIONS ) );

        String actualTopViolations = visitor.getResults().get( ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS );
        assertNotNull( actualTopViolations );
        assertEquals( topViolations, actualTopViolations );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleFileVisitor#runParse(java.io.File)}.
     */
    public final void testRunParseWithSimpleMerge()
    {
        simulateOldParsing( "" );

        visitor.runParse( new File( url.getFile() ) );

        assertViolations( "767", "188", "17", "972", ICvsHelperConst.SAMPLE_TOP_VIOLATIONS );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleFileVisitor#runParse(java.io.File)}.
     */
    public final void testRunParseWithMerge0to10()
    {
        // Simulate violation per file 0 to 10
        Map<String, Integer> oldTopViolationMap = new HashMap<String, Integer>();
        int i = 0;
        while ( i <= 10 )
        {
            oldTopViolationMap.put( "test" + i, i++ );
        }
        simulateOldParsing( ICvsHelperConst.SAMPLE_TOP_VIOLATIONS );

        // parse
        visitor.runParse( new File( url.getFile() ) );

        assertViolations( "767", "188", "17", "972", ICvsHelperConst.SAMPLE_TOP_VIOLATIONS );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleFileVisitor#runParse(java.io.File)}.
     */
    public final void testRunParseWithMergeMore366()
    {
        // Simulate violation per file 365 to 366
        Map<String, Integer> oldTopViolationMap = new HashMap<String, Integer>();
        int i = 0;
        while ( i <= 10 )
        {
            int tmp = 365 + i++;
            oldTopViolationMap.put( "test" + tmp, tmp );
        }
        simulateOldParsing( CsvHelper.convertTopViolationsToCsv( oldTopViolationMap ) );

        // parse
        visitor.runParse( new File( url.getFile() ) );

        String expectedCsv = "test375,375" + ICvsHelperConst.LINE_SEPARATOR 
        + "test374,374" + ICvsHelperConst.LINE_SEPARATOR 
        + "test373,373" + ICvsHelperConst.LINE_SEPARATOR
        + "test372,372" + ICvsHelperConst.LINE_SEPARATOR
        + "test371,371" + ICvsHelperConst.LINE_SEPARATOR
        + "test370,370" + ICvsHelperConst.LINE_SEPARATOR
        + "test369,369" + ICvsHelperConst.LINE_SEPARATOR
        + "test368,368" + ICvsHelperConst.LINE_SEPARATOR
        + "test367,367" + ICvsHelperConst.LINE_SEPARATOR
        + "test366,366" + ICvsHelperConst.LINE_SEPARATOR;
        
        assertViolations( "767", "188", "17", "972", expectedCsv );
    }


    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleFileVisitor#runParse(java.io.File)}.
     */
    public final void testRunParseWithMerge363()
    {
        // Simulate violation per file 365 to 366
        Map<String, Integer> oldTopViolationMap = new HashMap<String, Integer>();
        int i = 0;
        while ( i <= 10 )
        {
            int tmp = 363 + i++;
            oldTopViolationMap.put( "test" + tmp, tmp );
        }
        simulateOldParsing( CsvHelper.convertTopViolationsToCsv( oldTopViolationMap ) );

        // parse
        visitor.runParse( new File( url.getFile() ) );

        String expectedCsv = "test373,373" + ICvsHelperConst.LINE_SEPARATOR
        + "test372,372" + ICvsHelperConst.LINE_SEPARATOR
        + "test371,371" + ICvsHelperConst.LINE_SEPARATOR
        + "test370,370" + ICvsHelperConst.LINE_SEPARATOR
        + "test369,369" + ICvsHelperConst.LINE_SEPARATOR
        + "test368,368" + ICvsHelperConst.LINE_SEPARATOR
        + "test367,367" + ICvsHelperConst.LINE_SEPARATOR
        + "test366,366" + ICvsHelperConst.LINE_SEPARATOR
        + "/ProjectPath/src/main/java/packagename/grid/service/impl/GridServiceImpl.java,365" + ICvsHelperConst.LINE_SEPARATOR
        + "test365,365" + ICvsHelperConst.LINE_SEPARATOR;
        
        assertViolations( "767", "188", "17", "972", expectedCsv );
    }

    private void simulateOldParsing( String topViolation )
    {
        visitor.getResults().put( ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS, "3" );
        visitor.getResults().put( ICheckStyleBuildProcessor.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS, "3" );
        visitor.getResults().put( ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS, "3" );
        visitor.getResults().put( ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATIONS, "9" );

        visitor.getResults().put( ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS, topViolation );
    }
}
