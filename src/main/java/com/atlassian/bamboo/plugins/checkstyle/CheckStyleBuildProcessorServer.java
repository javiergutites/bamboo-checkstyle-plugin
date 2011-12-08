package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.bamboo.build.Build;
import com.atlassian.bamboo.build.BuildManager;
import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.resultsummary.BuildResultsSummaryManager;
import com.atlassian.bamboo.resultsummary.ExtendedBuildResultsSummary;
import com.atlassian.bamboo.v2.build.BuildContext;

/**
 * CheckStyleBuildProcessorServer.
 * 
 * @author lauvigne
 */
public class CheckStyleBuildProcessorServer
    implements CustomBuildProcessorServer, ICheckStyleBuildProcessor
{
    // =======================================================================================================
    // === ATTRIBUTES
    // =======================================================================================================
    private static final Logger log = Logger.getLogger( CheckStyleBuildProcessorServer.class );

    private BuildContext buildContext;

    /** Injected by Spring BuildResultsSummaryManager */
    private BuildResultsSummaryManager buildResultsSummaryManager;
    /** Injected by Spring BuildManager */
    private BuildManager buildManager;
    
    /**
     * @see com.atlassian.bamboo.v2.build.task.BuildTask#call()
     */
    public BuildContext call()
        throws InterruptedException, Exception
    {
        log.info( "Running CheckStyle BuildProcessor Server" );

        calculateDelta();

        return buildContext;
    }

    /**
     * @see com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin#init(com.atlassian.bamboo.v2.build.BuildContext)
     */
    public void init( BuildContext buildContext )
    {
        this.buildContext = buildContext;
    }

    /**
     * Affect to the currentBuild the delta of checkstyle violation calculate with the previous build
     */
    public void calculateDelta()
    {
        Map<String, String> result = buildContext.getBuildResult().getCustomBuildData();

        //put delta only if Checkstyle collect is OK
        if ( CheckstylePluginHelper.hasCheckstyleResults( result ) )
        {
            CheckStyleInformation currentInformation = new CheckStyleInformation( result );

            Build build = getBuildManager().getBuildByKey(buildContext.getPlanKey());
            
            ExtendedBuildResultsSummary previousSummary =
                getBuildResultsSummaryManager().getLastBuildSummary( build );
            
            //calculate delta only if an previous build is present
            if (previousSummary != null) {
                CheckStyleInformation previousInformation =
                    new CheckStyleInformation( previousSummary.getCustomBuildData() );
    
                currentInformation.setDeltaTotalViolations( previousInformation );
                currentInformation.setDeltaErrorViolations( previousInformation );
                currentInformation.setDeltaWarningViolations( previousInformation );
                currentInformation.setDeltaInfoViolations( previousInformation );
            }
        }
    }

    /**
     * @param buildManager the buildManager to set
     */
    public void setBuildManager( BuildManager buildManager )
    {
        this.buildManager = buildManager;
    }

    /**
     * @return the buildManager
     */
    public BuildManager getBuildManager()
    {
        return buildManager;
    }

    /**
     * @param buildResultsSummaryManager the buildResultsSummaryManager to set
     */
    public void setBuildResultsSummaryManager( BuildResultsSummaryManager buildResultsSummaryManager )
    {
        this.buildResultsSummaryManager = buildResultsSummaryManager;
    }

    /**
     * @return the buildResultsSummaryManager
     */
    public BuildResultsSummaryManager getBuildResultsSummaryManager()
    {
        return buildResultsSummaryManager;
    }
}
