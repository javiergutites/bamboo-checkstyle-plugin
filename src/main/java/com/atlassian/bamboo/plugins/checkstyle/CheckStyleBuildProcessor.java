package com.atlassian.bamboo.plugins.checkstyle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.bamboo.v2.build.BuildContextHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessor;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.utils.FileVisitor;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.error.SimpleErrorCollection;
import com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import com.atlassian.bamboo.v2.build.repository.RepositoryV2;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;

/**
 * CheckStyleBuildProcessor collect all checkstyle result.
 * 
 * @author lauvigne
 */
public class CheckStyleBuildProcessor
    extends BaseConfigurableBuildPlugin
    implements CustomBuildProcessor, ICheckStyleBuildProcessor
{
    /** logger */
    private static final Logger log = Logger.getLogger( CheckStyleBuildProcessor.class );
    /** Bamboo build logger */
    private BuildLoggerManager buildLoggerManager;

    /**
     * @see com.atlassian.bamboo.v2.build.task.BuildTask#call()
     */
    public BuildContext call()
        throws InterruptedException, Exception
    {
        log.info( "Running CheckStyle BuildProcessor" );
        final Map<String, String> checkstyleResults = new HashMap<String, String>();

        Map<String, String> customConfiguration = buildContext.getBuildDefinition().getCustomConfiguration();
        CurrentBuildResult buildResult = buildContext.getBuildResult();

        if ( CheckstylePluginHelper.isPluginActivated( customConfiguration ) )
        {
            String pathPattern = customConfiguration.get( CHECKSTYLE_XML_PATH_KEY );

            if ( !StringUtils.isEmpty( pathPattern ) )
            {
                File sourceDirectory = BuildContextHelper.getBuildWorkingDirectory(buildContext);

                FileVisitor fileVisitor = new CheckStyleFileVisitor( sourceDirectory, checkstyleResults );
                fileVisitor.visitFilesThatMatch( pathPattern );

                if ( !checkstyleResults.isEmpty() )
                {
                    if (StringUtils.isNotBlank( customConfiguration.get( CHECKSTYLE_SITE_URL ) ) ) {
                        // Transform filename in http checkstyle report
                        CheckstylePluginHelper.transformFilenameInHttpURL( sourceDirectory, customConfiguration, checkstyleResults );
                    }

                    // Check for thresholds on error and warning and fail build
                    // if exceeded
                    BuildLogger buildLogger = buildLoggerManager.getBuildLogger( buildContext.getPlanKey() );
                    CheckstylePluginHelper.processThreshold( buildContext, checkstyleResults, "error", buildLogger );
                    CheckstylePluginHelper.processThreshold( buildContext, checkstyleResults, "warning", buildLogger );

                    // Save
                    buildResult.getCustomBuildData().putAll( checkstyleResults );
                }
            }
        }

        return buildContext;
    }

    /**
     * @see com.atlassian.bamboo.v2.build.BaseConfigurablePlugin#validate(com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration)
     */
    public ErrorCollection validate( BuildConfiguration configuration)
    {
        ErrorCollection ec = new SimpleErrorCollection();
        if ( configuration.getBoolean( CHECKSTYLE_EXISTS )
                && StringUtils.isBlank( configuration.getString( CHECKSTYLE_PATH ) ) )
        {
            ec.addError( CHECKSTYLE_PATH, "Please specify the directory containing the XML CheckStyle output files." );
        }

        String msgValid = CheckstylePluginHelper.validCheckstyleURL( configuration );
        if ( msgValid != null )
        {
            ec.addError( CHECKSTYLE_PATH, "Base HTTP URL is invalid  :" + msgValid );
        }

        return ec;
    }

    /**
     * @see com.atlassian.bamboo.v2.build.BaseConfigurablePlugin#addDefaultValues(com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration)
     */
    public void addDefaultValues(@NotNull BuildConfiguration buildConfiguration)
    {
        if (buildConfiguration.getProperty( CHECKSTYLE_PATH ) == null) {
            buildConfiguration.setProperty( CHECKSTYLE_PATH, "**/target/checkstyle-result.xml" );
        }
    }
    

    ///CLOVER:OFF
    public void setBuildLoggerManager( BuildLoggerManager buildLoggerManager )
    {
        this.buildLoggerManager = buildLoggerManager;
    }
}
