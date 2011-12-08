/*
 * BAMBOO CheckStyle Plugin This plugin was developed on base of the PMD-plugin written by Ross Rowe.
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import com.atlassian.bamboo.build.Build;
import com.atlassian.bamboo.build.BuildManager;
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
    private BuildManager buildManager;

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

    public void setBuildManager( BuildManager buildManager )
    {
        this.buildManager = buildManager;
    }

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
        Build build = buildManager.getBuildByKey( buildKey );
        if ( build == null )
        {
            return false;
        }

        Map<String, String> customConfiguration = build.getBuildDefinition().getCustomConfiguration();
        return CheckstylePluginHelper.isPluginActivated( customConfiguration );
    }
}