package com.atlassian.bamboo.plugins.checkstyle.actions;

import com.atlassian.bamboo.build.BuildDefinitionManager;
import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.plugins.checkstyle.CheckstylePluginHelper;
import com.atlassian.bamboo.plugins.checkstyle.ICheckStyleBuildProcessor;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.ww2.BambooActionSupport;
import com.atlassian.bamboo.ww2.aware.permissions.GlobalAdminSecurityAware;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class MigrateAction extends BambooActionSupport implements GlobalAdminSecurityAware
{
    private BuildDefinitionManager buildDefinitionManager;
    private TaskConfigurationService taskConfigurationService;
    private TaskManager taskManager;

    @Override
    public String doExecute() throws Exception
    {
        for (Job job : planManager.getAllPlans(Job.class))
        {
            if (isCheckStyleEnabled(job))
            {
                Map<String, String> checkStyleConfig = Maps.filterKeys(job.getBuildDefinition().getCustomConfiguration(), new Predicate<String>() {
                    @Override
                    public boolean apply(String s) {
                        return s.startsWith("custom.checkstyle");
                    }
                });

                TaskModuleDescriptor taskModuleDescriptor = taskManager.getTaskDescriptor(CheckstylePluginHelper.CHECKSTYLE_TASK_PLUGIN_KEY);

                TaskDefinition taskDefinition = taskConfigurationService.createTask(job.getPlanKey(),
                        taskModuleDescriptor, "", true, checkStyleConfig, TaskRootDirectorySelector.DEFAULT);

                List<TaskDefinition> taskDefinitions = job.getBuildDefinition().getTaskDefinitions();
                taskDefinition.setFinalising(true);
                taskDefinitions.add(taskDefinition);

                buildDefinitionManager.savePlanAndDefinition(job, job.getBuildDefinition());
            }
        }

        return SUCCESS;
    }

    private boolean isCheckStyleEnabled(Job job)
    {
        Map<String, String> customConfiguration = job.getBuildDefinition().getCustomConfiguration();
        return customConfiguration != null && customConfiguration.containsKey( ICheckStyleBuildProcessor.CHECKSTYLE_EXISTS )
            && "true".equals( customConfiguration.get( ICheckStyleBuildProcessor.CHECKSTYLE_EXISTS ) )
            && customConfiguration.containsKey( ICheckStyleBuildProcessor.CHECKSTYLE_XML_PATH_KEY );
    }

    public void setTaskManager(TaskManager taskManager)
    {
        this.taskManager = taskManager;
    }

    public void setTaskConfigurationService(TaskConfigurationService taskConfigurationService)
    {
        this.taskConfigurationService = taskConfigurationService;
    }

    public void setBuildDefinitionManager(BuildDefinitionManager buildDefinitionManager)
    {
        this.buildDefinitionManager = buildDefinitionManager;
    }
}
