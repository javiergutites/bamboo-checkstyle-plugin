package com.atlassian.bamboo.plugins.checkstyle;

import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.artifact.ArtifactDefinition;
import com.atlassian.bamboo.plan.artifact.ArtifactDefinitionImpl;
import com.atlassian.bamboo.plan.artifact.ArtifactDefinitionManager;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.v2.build.BaseConfigurablePlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.agent.capability.RequirementSet;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * CheckStyleBuildProcessorServer.
 *
 * @author lauvigne
 */
public class CheckStyleBuildProcessorServer extends BaseConfigurablePlugin
        implements CustomBuildProcessorServer, ICheckStyleBuildProcessor
{
    public static final String CHECKSTYLE_ENABLE_INTEGRATION = "custom.checkstyle.enable.integration";

    public static final String CHECKSTYLE_JSON_ARTIFACT_NAME = "Checkstyle JSON Report (System)";
    public static final String CHECKSTYLE_JSON_ARTIFACT_LOCATION = "checkstyle-json";
    public static final String CHECKSTYLE_JSON_ARTIFACT_FILE = "checkstyle.json";

    // =======================================================================================================
    // === ATTRIBUTES
    // =======================================================================================================
    private static final Logger log = Logger.getLogger( CheckStyleBuildProcessorServer.class );

    private BuildContext buildContext;

    private ResultsSummaryManager resultsSummaryManager;
    private ArtifactDefinitionManager artifactDefinitionManager;
    private PlanManager planManager;

    /**
     * @see com.atlassian.bamboo.v2.build.task.BuildTask#call()
     */
    public BuildContext call()
            throws InterruptedException, Exception
    {
        log.info("Running CheckStyle BuildProcessor Server");

        calculateDelta();

        return buildContext;
    }

    /**
     * @see com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin#init(com.atlassian.bamboo.v2.build.BuildContext)
     */
    public void init(BuildContext buildContext)
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

    @Override
    protected void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull BuildConfiguration buildConfiguration, @NotNull Plan plan) {
        super.populateContextForEdit(context, buildConfiguration, plan);

        context.put(CHECKSTYLE_ENABLE_INTEGRATION, buildConfiguration.getBoolean(CHECKSTYLE_ENABLE_INTEGRATION));
    }

    @Override
    protected void populateContextForView(@NotNull Map<String, Object> context, @NotNull Plan plan) {
        super.populateContextForView(context, plan);

        // TODO ?
//        context.put(CHECKSTYLE_ENABLE_INTEGRATION, plan)
    }

    @Override
    public void customizeBuildRequirements(@NotNull PlanKey planKey, @NotNull BuildConfiguration buildConfiguration, @NotNull RequirementSet requirementSet) {
        super.customizeBuildRequirements(planKey, buildConfiguration, requirementSet);

        final Job job = planManager.getPlanByKeyIfOfType(planKey, Job.class);
        if (job == null) {
            return;
        }

        boolean isIntegrationEnabled = buildConfiguration.getBoolean(CHECKSTYLE_ENABLE_INTEGRATION);
        ArtifactDefinition existingDefinition = artifactDefinitionManager.findArtifactDefinition(job, CHECKSTYLE_JSON_ARTIFACT_NAME);
        boolean hasArtifactDefinition = existingDefinition != null;

        if (isIntegrationEnabled != hasArtifactDefinition) {
            if (isIntegrationEnabled) {
                // create the definition
                ArtifactDefinitionImpl artifact = new ArtifactDefinitionImpl(CHECKSTYLE_JSON_ARTIFACT_NAME,
                        CHECKSTYLE_JSON_ARTIFACT_LOCATION, CHECKSTYLE_JSON_ARTIFACT_FILE);
                artifact.setProducerJob(job);
                artifactDefinitionManager.saveArtifactDefinition(artifact);
            } else {
                artifactDefinitionManager.removeArtifactDefinition(existingDefinition);
            }
        }
    }

    public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager)
    {
        this.resultsSummaryManager = resultsSummaryManager;
    }

    public void setArtifactDefinitionManager(ArtifactDefinitionManager artifactDefinitionManager) {
        this.artifactDefinitionManager = artifactDefinitionManager;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }
}
