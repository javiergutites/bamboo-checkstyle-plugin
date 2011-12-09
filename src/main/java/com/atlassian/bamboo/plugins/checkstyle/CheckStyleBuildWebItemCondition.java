/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

/************************************************************************************************************
 * CheckStyleBuildWebItemCondition.
 * 
 * @author Stephan Paulicke
 */
public class CheckStyleBuildWebItemCondition
    implements Condition
{
    // =======================================================================================================
    // === ATTRIBUTES
    // =======================================================================================================
    private PlanManager planManager;

    // =======================================================================================================
    // === METHODS
    // =======================================================================================================

    /********************************************************************************************************
     * Initialize.
     * 
     * @param map
     */
    @SuppressWarnings( "unchecked" )
    public void init( Map map )
        throws PluginParseException
    {
        // --- nothing to do here
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
        if ( buildKey == null )
        {
            return false;
        }

        Job build = planManager.getPlanByKey(buildKey, Job.class);
        if ( build == null )
        {
            return false;
        }

        return CheckstylePluginHelper.isPluginActivated( build );
    }

    public void setPlanManager(PlanManager planManager)
    {
        this.planManager = planManager;
    }
}