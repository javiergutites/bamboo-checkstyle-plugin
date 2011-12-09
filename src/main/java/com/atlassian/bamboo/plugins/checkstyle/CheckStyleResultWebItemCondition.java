/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.resultsummary.BuildResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
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
    private ResultsSummaryManager resultsSummaryManager;

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
            ( context.get( ICheckStyleBuildProcessor.BUILD_KEY ) == null ) ? null
                            : (String) context.get( ICheckStyleBuildProcessor.BUILD_KEY );
        String buildNumberString =
            ( context.get( ICheckStyleBuildProcessor.BUILD_NUMBER ) == null ) ? null
                            : (String) context.get( ICheckStyleBuildProcessor.BUILD_NUMBER );
        if ( ( buildKey == null ) || ( buildNumberString == null ) )
        {
            return false;
        }
        
        int buildNumber = Integer.parseInt(buildNumberString);
        PlanResultKey planResultKey = PlanKeys.getPlanResultKey(buildKey, buildNumber);
        ResultsSummary buildResults = resultsSummaryManager.getResultsSummary(planResultKey);
        if ( buildResults == null )
        {
            return false;
        }

        return CheckstylePluginHelper.hasCheckstyleResults( buildResults );
    }

    public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager)
    {
        this.resultsSummaryManager = resultsSummaryManager;
    }
}