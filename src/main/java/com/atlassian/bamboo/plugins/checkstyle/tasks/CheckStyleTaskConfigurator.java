package com.atlassian.bamboo.plugins.checkstyle.tasks;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class CheckStyleTaskConfigurator extends AbstractTaskConfigurator
{
    public static final String CHECKSTYLE_PATH = "custom.checkstyle.path";
    public static final String CHECKSTYLE_SITE_URL = "custom.checkstyle.site.url";
    public static final String CHECKSTYLE_ERROR_PRIORITY_THRESHOLD = "custom.checkstyle.threshold.error";
    public static final String CHECKSTYLE_WARNING_PRIORITY_THRESHOLD = "custom.checkstyle.threshold.warning";

    private static final Set<String> FIELDS = ImmutableSet.of(
            CHECKSTYLE_PATH,
            CHECKSTYLE_SITE_URL,
            CHECKSTYLE_ERROR_PRIORITY_THRESHOLD,
            CHECKSTYLE_WARNING_PRIORITY_THRESHOLD);

    @Override
    public void populateContextForCreate(@NotNull Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        context.put(CHECKSTYLE_PATH, "**/target/checkstyle-result.xml");
    }

    @Override
    public void populateContextForView(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, FIELDS);
    }

    @Override
    public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        taskConfiguratorHelper.populateContextWithConfiguration(context, taskDefinition, FIELDS);
    }

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull ActionParametersMap params, @Nullable TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        taskConfiguratorHelper.populateTaskConfigMapWithActionParameters(config, params, FIELDS);
        return config;
    }

    @Override
    public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        if (StringUtils.isEmpty(params.getString(CheckStyleTaskConfigurator.CHECKSTYLE_PATH)))
        {
            errorCollection.addError(CheckStyleTaskConfigurator.CHECKSTYLE_PATH, "Please specify the directory containing the XML CheckStyle output files.");
        }
    }
}
