package com.atlassian.bamboo.plugins.checkstyle.tasks;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.plugins.checkstyle.CheckStyleFileVisitor;
import com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginHelper;
import com.atlassian.bamboo.plugins.checkstyle.ICheckStyleBuildProcessor;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.utils.FileVisitor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CheckStyleTask implements TaskType
{
    @NotNull
    @Override
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException
    {
        final TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        final ConfigurationMap config = taskContext.getConfigurationMap();

        final Map<String, String> checkstyleResults = new HashMap<String, String>();

        String pathPattern = config.get(ICheckStyleBuildProcessor.CHECKSTYLE_XML_PATH_KEY);

        if (!StringUtils.isEmpty(pathPattern))
        {
            File workingDirectory = taskContext.getWorkingDirectory();
            FileVisitor fileVisitor = new CheckStyleFileVisitor(workingDirectory, checkstyleResults);
            try
            {
                fileVisitor.visitFilesThatMatch(pathPattern);
            }
            catch (InterruptedException e)
            {
                throw new TaskException("Could not find checkstyle files: " + e.getMessage(), e);
            }

            if (!checkstyleResults.isEmpty())
            {
                if (StringUtils.isNotBlank(config.get(CheckStyleTaskConfigurator.CHECKSTYLE_SITE_URL))) {
                    // Transform filename in http checkstyle report
                    CheckstylePluginHelper.transformFilenameInHttpURL(workingDirectory, config, checkstyleResults);
                }

                // Check for thresholds on error and warning and fail build
                // if exceeded

                processThreshold(config, checkstyleResults, "error", buildLogger, builder);
                processThreshold(config, checkstyleResults, "warning", buildLogger, builder);

                taskContext.getBuildContext().getBuildResult().getCustomBuildData().putAll(checkstyleResults);
            }
        }

        return builder.build();
    }

    private void processThreshold(ConfigurationMap config, Map<String, String> checkstyleResults, String type,
                                  BuildLogger buildLogger, TaskResultBuilder builder)
    {
        String thresholdName = CheckStyleTaskConfigurator.CHECKSTYLE_ERROR_PRIORITY_THRESHOLD;
        String violationName = ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS;
        if ( "warning".equals( type ) )
        {
            thresholdName = CheckStyleTaskConfigurator.CHECKSTYLE_WARNING_PRIORITY_THRESHOLD;
            violationName = ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS;
        }

        int threshold = CheckstylePluginHelper.getThreshold( config.get( thresholdName ) );
        double violations = NumberUtils.toDouble(checkstyleResults.get(violationName), 0);

        if ( threshold >= 0 && violations > threshold )
        {
            String msg = String.format( "Checkstyle %s violations [%s] exceed threshold [%s]", type, violations, threshold );

            buildLogger.addErrorLogEntry(msg);

            builder.failed();
        }
    }
}
