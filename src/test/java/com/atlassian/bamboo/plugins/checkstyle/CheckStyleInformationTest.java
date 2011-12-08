/**
 * 
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author lauvigne
 */
public class CheckStyleInformationTest
    extends TestCase
{

    private CheckStyleInformation checkStyleInformation = null;

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        checkStyleInformation = new CheckStyleInformation( new HashMap<String, String>() );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#getTopViolations()}.
     */
    public final void testGetTopViolations()
    {
        assertNull( checkStyleInformation.getTopViolations() );
        String expected = "test1";
        checkStyleInformation.setTopViolations( expected );
        assertEquals( expected, checkStyleInformation.getTopViolations() );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#getTotalViolations()}.
     */
    public final void testGetTotalViolations()
    {
        assertEquals( new Integer( 0 ), checkStyleInformation.getTotalViolations() );
        Integer expected = new Integer( 1 );
        checkStyleInformation.setTotalViolations( expected );
        assertEquals( expected, checkStyleInformation.getTotalViolations() );

    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#getErrorViolations()}.
     */
    public final void testGetErrorViolations()
    {
        assertEquals( new Integer( 0 ), checkStyleInformation.getErrorViolations() );
        Integer expected = 1;
        checkStyleInformation.setErrorViolations( expected );
        assertEquals( expected, checkStyleInformation.getErrorViolations() );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#getWarningViolations()}.
     */
    public final void testGetWarningViolations()
    {
        assertEquals( new Integer( 0 ), checkStyleInformation.getWarningViolations() );
        Integer expected = 1;
        checkStyleInformation.setWarningViolations( expected );
        assertEquals( expected, checkStyleInformation.getWarningViolations() );
    }

    /**
     * Test method for {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#getInfoViolations()}.
     */
    public final void testGetInfoViolations()
    {
        assertEquals( new Integer( 0 ), checkStyleInformation.getInfoViolations() );
        Integer expected = 1;
        checkStyleInformation.setInfoViolations( expected );
        assertEquals( expected, checkStyleInformation.getInfoViolations() );
    }

    /**
     * Test method for
     * {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#setDeltaTotalViolations(com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation)}
     * .
     */
    public final void testSetDeltaTotalViolations()
    {
        checkStyleInformation.setDeltaTotalViolations( checkStyleInformation );
        assertEquals(
                      "0",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA ) );

        CheckStyleInformation previous = new CheckStyleInformation( new HashMap<String, String>() );
        previous.setTotalViolations( 10 );
        checkStyleInformation.setDeltaTotalViolations( previous );
        assertEquals(
                      "-10",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA ) );

        checkStyleInformation.setTotalViolations( 4 );
        previous.setTotalViolations( 3 );
        checkStyleInformation.setDeltaTotalViolations( previous );
        assertEquals(
                      "1",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA ) );
    }

    /**
     * Test method for
     * {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#setDeltaErrorViolations(com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation)}
     * .
     */
    public final void testSetDeltaErrorViolations()
    {
        checkStyleInformation.setDeltaErrorViolations( checkStyleInformation );
        assertEquals(
                      "0",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_VIOLATION_DELTA ) );

        CheckStyleInformation previous = new CheckStyleInformation( new HashMap<String, String>() );
        previous.setErrorViolations( 10 );
        checkStyleInformation.setDeltaErrorViolations( previous );
        assertEquals(
                      "-10",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_VIOLATION_DELTA ) );

        checkStyleInformation.setErrorViolations( 4 );
        previous.setErrorViolations( 3 );
        checkStyleInformation.setDeltaErrorViolations( previous );
        assertEquals(
                      "1",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_VIOLATION_DELTA ) );
    }

    /**
     * Test method for
     * {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#setDeltaWarningViolations(com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation)}
     * .
     */
    public final void testSetDeltaWarningViolations()
    {
        checkStyleInformation.setDeltaWarningViolations( checkStyleInformation );
        assertEquals(
                      "0",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_VIOLATION_DELTA ) );

        CheckStyleInformation previous = new CheckStyleInformation( new HashMap<String, String>() );
        previous.setWarningViolations( 10 );
        checkStyleInformation.setDeltaWarningViolations( previous );
        assertEquals(
                      "-10",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_VIOLATION_DELTA ) );

        checkStyleInformation.setWarningViolations( 4 );
        previous.setWarningViolations( 3 );
        checkStyleInformation.setDeltaWarningViolations( previous );
        assertEquals(
                      "1",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_VIOLATION_DELTA ) );
    }

    /**
     * Test method for
     * {@link com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation#setDeltaInfoViolations(com.atlassian.bamboo.plugins.checkstyle.CheckStyleInformation)}
     * .
     */
    public final void testSetDeltaInfoViolations()
    {
        checkStyleInformation.setDeltaInfoViolations( checkStyleInformation );
        assertEquals(
                      "0",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_INFO_VIOLATION_DELTA ) );

        CheckStyleInformation previous = new CheckStyleInformation( new HashMap<String, String>() );
        previous.setInfoViolations( 10 );
        checkStyleInformation.setDeltaInfoViolations( previous );
        assertEquals(
                      "-10",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_INFO_VIOLATION_DELTA ) );

        checkStyleInformation.setInfoViolations( 4 );
        previous.setInfoViolations( 3 );
        checkStyleInformation.setDeltaInfoViolations( previous );
        assertEquals(
                      "1",
                      checkStyleInformation.getCustomdata().get(
                                                                 ICheckStyleBuildProcessor.CHECKSTYLE_INFO_VIOLATION_DELTA ) );
    }
}
