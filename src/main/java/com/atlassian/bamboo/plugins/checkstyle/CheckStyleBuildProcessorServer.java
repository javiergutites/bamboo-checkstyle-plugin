package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import org.apache.log4j.Logger;

import com.atlassian.bamboo.build.CustomBuildProcessorServer;
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

    private ResultsSummaryManager resultsSummaryManager;

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

            ResultsSummary previousSummary =
                resultsSummaryManager.getLastResultsSummary(buildContext.getPlanKey(), ResultsSummary.class );
            
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

    public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager)
    {
        this.resultsSummaryManager = resultsSummaryManager;
    }
}
