package com.atlassian.bamboo.plugins.checkstyle;

import com.atlassian.bamboo.build.CustomBuildProcessorServer;
import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.build.artifact.ArtifactLinkManager;
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

import javax.annotation.Nonnull;
import java.util.Map;

import static com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginConstants.*;

/**
 * CheckStyleBuildProcessorServer.
 *
 * @author lauvigne
 */
public class CheckStyleBuildProcessorServer extends BaseConfigurablePlugin implements CustomBuildProcessorServer
{
    private static final Logger log = Logger.getLogger( CheckStyleBuildProcessorServer.class );

    private BuildContext buildContext;

    private ResultsSummaryManager resultsSummaryManager;
    private ArtifactLinkManager artifactLinkManager;
    private ArtifactDefinitionManager artifactDefinitionManager;
    private PlanManager planManager;

    @Nonnull
    public BuildContext call() throws InterruptedException, Exception {
        log.info("Running CheckStyle BuildProcessor Server");

        calculateDelta();
        sendNotification();

        return buildContext;
    }

    public void init(@Nonnull BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    /**
     * Affect to the currentBuild the delta of checkstyle violation calculate with the previous build
     */
    public void calculateDelta()
    {
        Map<String, String> result = buildContext.getBuildResult().getCustomBuildData();

        //put delta only if Checkstyle collect is OK
        if (CheckstylePluginHelper.hasCheckstyleResults(result))
        {
            CheckStyleInformation currentInformation = new CheckStyleInformation(result);

            ResultsSummary previousSummary =
                    resultsSummaryManager.getLastResultsSummary(buildContext.getPlanKey(), ResultsSummary.class );

            //calculate delta only if an previous build is present
            if (previousSummary != null) {
                CheckStyleInformation previousInformation =
                        new CheckStyleInformation(previousSummary.getCustomBuildData());

                currentInformation.setDeltaTotalViolations(previousInformation);
                currentInformation.setDeltaErrorViolations(previousInformation);
                currentInformation.setDeltaWarningViolations(previousInformation);
                currentInformation.setDeltaInfoViolations(previousInformation);
            }
        }
    }

    @Override
    public void customizeBuildRequirements(@NotNull PlanKey planKey, @NotNull BuildConfiguration buildConfiguration, @NotNull RequirementSet requirementSet) {
        super.customizeBuildRequirements(planKey, buildConfiguration, requirementSet);

        final Job job = planManager.getPlanByKeyIfOfType(planKey, Job.class);
        if (job == null) {
            return;
        }

        boolean isIntegrationEnabled = buildConfiguration.getBoolean(CHECKSTYLE_ENABLE_INTEGRATION);
        ArtifactDefinition existingDefinition = artifactDefinitionManager.findArtifactDefinition(job, CHECKSTYLE_JSON_ARTIFACT_LABEL);
        boolean hasArtifactDefinition = existingDefinition != null;

        if (isIntegrationEnabled != hasArtifactDefinition) {
            if (isIntegrationEnabled) {
                // create the definition
                ArtifactDefinitionImpl artifact = new ArtifactDefinitionImpl(CHECKSTYLE_JSON_ARTIFACT_LABEL,
                        CHECKSTYLE_JSON_ARTIFACT_LOCATION, CHECKSTYLE_JSON_ARTIFACT_FILE_NAME);
                artifact.setProducerJob(job);
                artifactDefinitionManager.saveArtifactDefinition(artifact);
            } else {
                artifactDefinitionManager.removeArtifactDefinition(existingDefinition);
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

        // TODO how can we do it without build config wut?
//        context.put(CHECKSTYLE_ENABLE_INTEGRATION, plan)
    }

    private void sendNotification() {
        if (CheckstylePluginHelper.isIntegrationEnabled(buildContext) && isSuccessful()) {

        }
    }

    private boolean isSuccessful() {
        // TODO it is ridiculously hard to figre out whether the build actually succeeded :/
        return true;
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
