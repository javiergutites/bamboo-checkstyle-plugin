/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.resultsummary.BuildResultsSummaryManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

/************************************************************************************************************
 * CheckStyleResultWebItemCondition.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleResultWebItemCondition
    implements Condition
{
    // =======================================================================================================
    // === ATTRIBUTES
    // =======================================================================================================
    /** Injected by Spring */
    private BuildResultsSummaryManager buildResultsSummaryManager;

    // =======================================================================================================
    // === METHODS
    // =======================================================================================================

    /********************************************************************************************************
     * init().
     * 
     * @param map
     */
    @SuppressWarnings( "unchecked" )
    public void init( Map map )
        throws PluginParseException
    {
    }

    // =======================================================================================================
    // === PROPERTIES
    // =======================================================================================================

    @SuppressWarnings( "unchecked" )
    public boolean shouldDisplay( Map context )
    {
        String buildKey =
            ( context.get( CheckStyleBuildProcessor.BUILD_KEY ) == null ) ? null
                            : (String) context.get( CheckStyleBuildProcessor.BUILD_KEY );
        String buildNumberString =
            ( context.get( CheckStyleBuildProcessor.BUILD_NUMBER ) == null ) ? null
                            : (String) context.get( CheckStyleBuildProcessor.BUILD_NUMBER );
        if ( ( buildKey == null ) || ( buildNumberString == null ) )
        {
            return false;
        }
        
        int buildNumber = Integer.parseInt(buildNumberString);
        BuildResultsSummary buildResults = buildResultsSummaryManager.getBuildResultsSummary(buildKey, buildNumber);
        if ( buildResults == null )
        {
            return false;
        }

        return CheckstylePluginHelper.hasCheckstyleResults( buildResults );
    }

    public void setBuildResultsSummaryManager(BuildResultsSummaryManager buildResultsSummaryManager)
    {
        this.buildResultsSummaryManager = buildResultsSummaryManager;
    }
}