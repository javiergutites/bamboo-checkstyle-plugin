package com.atlassian.bamboo.plugins.checkstyle.tasks;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginConstants;
import com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginHelper;
import com.atlassian.bamboo.plugins.checkstyle.parser.DetailedJsonCheckstyleResultProcessor;
import com.atlassian.bamboo.task.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginConstants.CHECKSTYLE_JSON_ARTIFACT_FILE_NAME;
import static com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginConstants.CHECKSTYLE_JSON_ARTIFACT_LOCATION;

public class CheckStyleTask implements TaskType
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @NotNull
    @Override
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException
    {
        final TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        final ConfigurationMap config = taskContext.getConfigurationMap();

        final String pathPattern = config.get(CheckstylePluginConstants.CHECKSTYLE_XML_PATH_KEY);
        final boolean integrationEnabled = CheckstylePluginHelper.isIntegrationEnabled(taskContext.getBuildContext());

        final Map<String, String> checkstyleResults = new HashMap<String, String>();


        if (!StringUtils.isEmpty(pathPattern))
        {
            File workingDirectory = taskContext.getWorkingDirectory();
            CheckStyleFileVisitor fileVisitor = new CheckStyleFileVisitor(workingDirectory, checkstyleResults);

            DetailedJsonCheckstyleResultProcessor jsonProcessor = new DetailedJsonCheckstyleResultProcessor();
            if (integrationEnabled) {
                fileVisitor = fileVisitor.withProcessors(jsonProcessor);
            }

            visitPaths(pathPattern, fileVisitor);

            if (!checkstyleResults.isEmpty())
            {
                if (StringUtils.isNotBlank(config.get(CheckStyleTaskConfigurator.CHECKSTYLE_SITE_URL))) {
                    // Transform filename in http checkstyle report
                    CheckstylePluginHelper.transformFilenameInHttpURL(workingDirectory, config, checkstyleResults);
                }

                // check for exceeded thresholds
                processThreshold(config, checkstyleResults, "error", buildLogger, builder);
                processThreshold(config, checkstyleResults, "warning", buildLogger, builder);

                taskContext.getBuildContext().getBuildResult().getCustomBuildData().putAll(checkstyleResults);

                if (integrationEnabled) {
                    storeJsonArtifact(jsonProcessor, workingDirectory, buildLogger);
                }
            }
        }

        return builder.build();
    }

    private void visitPaths(String pathPattern, CheckStyleFileVisitor fileVisitor) throws TaskException {
        try
        {
            fileVisitor.visitFilesThatMatch(pathPattern);
        }
        catch (InterruptedException e)
        {
            throw new TaskException("Could not find checkstyle files: " + e.getMessage(), e);
        }
        fileVisitor.finished();
    }

    private void processThreshold(ConfigurationMap config, Map<String, String> checkstyleResults, String type,
                                  BuildLogger buildLogger, TaskResultBuilder builder)
    {
        String thresholdName = CheckStyleTaskConfigurator.CHECKSTYLE_ERROR_PRIORITY_THRESHOLD;
        String violationName = CheckstylePluginConstants.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS;
        if ( "warning".equals( type ) )
        {
            thresholdName = CheckStyleTaskConfigurator.CHECKSTYLE_WARNING_PRIORITY_THRESHOLD;
            violationName = CheckstylePluginConstants.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS;
        }

        int threshold = CheckstylePluginHelper.getThreshold( config.get( thresholdName ) );
        double violations = NumberUtils.toDouble(checkstyleResults.get(violationName), 0);

        if (threshold >= 0 && violations > threshold)
        {
            String msg = String.format( "Checkstyle %s violations [%s] exceed threshold [%s]", type, violations, threshold );
            buildLogger.addErrorLogEntry(msg);
            builder.failed();
        }
    }

    private void storeJsonArtifact(DetailedJsonCheckstyleResultProcessor jsonProcessor, File workingDirectory,
                                   BuildLogger buildLogger) {
        try {
            File jsonArtifactDir = new File(workingDirectory, CHECKSTYLE_JSON_ARTIFACT_LOCATION);
            FileUtils.forceMkdir(jsonArtifactDir);
            MAPPER.writeValue(new File(jsonArtifactDir, CHECKSTYLE_JSON_ARTIFACT_FILE_NAME),
                    jsonProcessor.getNotifications());
        } catch (Exception e) {
            buildLogger.addErrorLogEntry("Unable to store JSON Checkstyle artifact", e);
        }
    }
}
