/**
 * 
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.plugins.checkstyle.tasks.CheckStyleTaskConfigurator;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.task.TaskDefinition;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.ww2.actions.build.admin.create.BuildConfiguration;
import org.aspectj.weaver.Iterators;

/**
 * Helper class with some common method for Checkstyle plugin
 * 
 * @author lauvigne
 */
public class CheckstylePluginHelper
    implements ICheckStyleBuildProcessor
{
    private static final String CHECKSTYLE_TASK_PLUGIN_KEY = "com.atlassian.bamboo.plugins.checkstyle:checkStyleTask";

    /**
     * @param job that
     * @return true if the plugin is actif for plan associated to this customConfiguration
     */
    public static boolean isPluginActivated( Job job )
    {
        return Iterables.find(job.getBuildDefinition().getTaskDefinitions(), new Predicate<TaskDefinition>() {
            @Override
            public boolean apply(TaskDefinition taskDefinition) {
                return taskDefinition.getPluginKey().equals(CHECKSTYLE_TASK_PLUGIN_KEY);
            }
        }, null) != null;
    }

    /**
     * @param customBuildData Map that constains the metadata of a build
     * @return true if the build has checkstyle results
     */
    public static boolean hasCheckstyleResults( Map<String, String> customBuildData )
    {
        return customBuildData != null
            && customBuildData.containsKey( CheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATIONS );
    }

    /**
     * @param summary a result of a build
     * @return true if the build has checkstyle results
     */
    public static boolean hasCheckstyleResults( ResultsSummary summary )
    {
        if ( summary == null )
        {
            return false;
        }
        else
        {
            Map<String, String> customBuildData = summary.getCustomBuildData();
            return hasCheckstyleResults( customBuildData );
        }
    }

    /**
     * @param configuration The build plan configuration
     * @return null if the url is valid, the error message if is invalid
     */
    public static String validCheckstyleURL( BuildConfiguration configuration )
    {
        if ( configuration.getBoolean( CHECKSTYLE_EXISTS )
            && StringUtils.isNotBlank( configuration.getString( CheckStyleTaskConfigurator.CHECKSTYLE_SITE_URL ) ) )
        {
            String url = configuration.getString( CheckStyleTaskConfigurator.CHECKSTYLE_SITE_URL );

            return validHttpURL( url );
        }

        // Else it's valid
        return null;
    }

    /**
     * @param url the url to test
     * @return null if the url is valid, the error message if is invalid
     */
    protected static String validHttpURL( String url )
    {
        try
        {
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout( 2 * 1000 );

            GetMethod get = new GetMethod( url );
            get.setFollowRedirects( true );

            int resultCode;

            resultCode = client.executeMethod( get );

            if ( resultCode != 200 )
            {
                return get.getResponseBodyAsString();
            }
        }
        catch ( Exception e )
        {
            return ExceptionUtils.getMessage( e );
        }

        // Else it's valid
        return null;
    }

    /**
     * Transform topViolations with filename by topViolations with Http URL.
     * 
     * @param sourceDirectory the working directory where the name has ran
     * @param customConfiguration Map that constains the metadata of a plan configuration
     * @param checkstyleResults map that contains the checkStyle results
     */
    public static void transformFilenameInHttpURL( File sourceDirectory,
                                                   Map<String, String> customConfiguration,
                                                   Map<String, String> checkstyleResults )
    {
        String baseURL = customConfiguration.get( CheckStyleTaskConfigurator.CHECKSTYLE_SITE_URL );
        String topViolations = checkstyleResults.get( ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS );

        String newTopViolations = transformFilenameInHttpURL( sourceDirectory, baseURL, topViolations );

        checkstyleResults.put( ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS, newTopViolations );
    }

    /**
     * @param topViolation the top violation content with absolute filename
     * @return the topViolation with http URL
     */
    public static String transformFilenameInHttpURL( File sourceDirectory, String baseURL,
                                                     String topViolation )
    {
        Map<String, Integer> topViolationInitial = CsvHelper.extractToCsv( topViolation );
        Map<String, Integer> topViolationFinal = new HashMap<String, Integer>();

        Iterator<Entry<String, Integer>> it = topViolationInitial.entrySet().iterator();
        while ( it.hasNext() )
        {
            Entry<String, Integer> entry = it.next();
            topViolationFinal.put( convertFilenameInHttpURL( sourceDirectory, baseURL, entry.getKey() ),
                                   entry.getValue() );

        }

        return CsvHelper.convertTopViolationsToCsv( topViolationFinal );
    }

    /**
     * TODO improve performance and design Be carefull, this method works only for maven2 report.
     * 
     * @param sourceDirectory
     * @param baseURL
     * @param key
     * @return the string represent the http URL
     */
    public static String convertFilenameInHttpURL( File sourceDirectory, String baseURL, String key )
    {
        String newBaseURL = StringUtils.trimToEmpty(baseURL);
        newBaseURL = StringUtils.removeEnd( newBaseURL, "/" );
        String filename = FilenameUtils.normalize( key );
        String beginPath = StringUtils.remove( filename, sourceDirectory.getPath() );

        beginPath = beginPath.replace( '\\', '/' );
        String sourceDir = "src/main/java/";

        String[] splitUrl = StringUtils.splitByWholeSeparator( beginPath, sourceDir );

        beginPath = splitUrl[0].replaceAll( "//", "/" );
        String classname = splitUrl[1].replace( '/', '.' );

        String result = newBaseURL + beginPath + "checkstyle.html#" + classname;

        return result;
    }
    

    /**
     * Converts String to int
     * 
     * @return -1 if parse exception or non-positive integer else returns
     *         integer from String
     */
    public static int getThreshold(
        String value)
    {
        int returnValue = NumberUtils.toInt( value, -1 );
        if ( returnValue < 0 )
        {
            returnValue = -1;
        }
        return returnValue;
    }

    public static void processThreshold(
        BuildContext context, Map<String, String> checkstyleResults, String type, BuildLogger buildLogger)
    {
        Map<String, String> customConfiguration = context.getBuildDefinition().getCustomConfiguration();
        String thresholdName = CheckStyleTaskConfigurator.CHECKSTYLE_ERROR_PRIORITY_THRESHOLD;
        String violationName = ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS;
        if ( "warning".equals( type ) )
        {
            thresholdName = CheckStyleTaskConfigurator.CHECKSTYLE_WARNING_PRIORITY_THRESHOLD;
            violationName = ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS;
        }
        
        int threshold = CheckstylePluginHelper.getThreshold( customConfiguration.get( thresholdName ) );
        double violations = NumberUtils.toDouble( checkstyleResults.get( violationName ), 0 );
        
        if ( threshold >= 0 && violations > threshold )
        {
            String msg = String.format( "Checkstyle %s violations [%s] exceed threshold [%s]", type, violations, threshold );
            
            //Save Why the build failed in build result
            context.getBuildResult().getBuildErrors().add( msg );
            context.getBuildResult().getCustomBuildData().put( "failedMessage", msg );
            
            //Save Why the build failed in the build log
            buildLogger.addErrorLogEntry( msg );
            
            //Failed this build
            context.getBuildResult().setBuildState( BuildState.FAILED );
        }
    }
}
