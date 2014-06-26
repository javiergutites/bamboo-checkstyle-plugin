package com.atlassian.bamboo.plugins.checkstyle;

import com.atlassian.bamboo.plugins.checkstyle.parser.CheckStyleReportParser;
import com.atlassian.bamboo.utils.FileVisitor;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CheckStyleFileVisitor
    extends FileVisitor
    implements ICheckStyleBuildProcessor
{
    private static final Logger log = Logger.getLogger( CheckStyleFileVisitor.class );

    private final Map<String, String> results;

    public CheckStyleFileVisitor( File file, Map<String, String> results )
    {
        super( file );
        this.results = results;
    }

    public void visitFile( File file )
    {
        if ( file.getName().endsWith( "xml" ) )
        {
            runParse( file );
        }
    }

    public void runParse( final File file )
    {
        try
        {
            log.info("Parsing file: " + file.getAbsolutePath());
            CheckStyleReportParser parser = new CheckStyleReportParser();
            parser.parse(file);

            merge(CHECKSTYLE_TOP_VIOLATIONS, parser.convertTopViolationsToCsv() );
            merge(CHECKSTYLE_TOTAL_VIOLATIONS, parser.getTotalViolations() );
            merge(CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS, parser.getErrorPriorityViolations() );
            merge(CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS, parser.getWarningPriorityViolations() );
            merge(CHECKSTYLE_INFO_PRIORITY_VIOLATIONS, parser.getInfoPriorityViolations() );
        }
        catch ( Exception e )
        {
            log.error( "Failed to parse artifact result file \"" + file.getName() + "\"", e );
        }
    }

    private void merge(String checkstyleViolations, long violations)
    {
        log.debug("Appending current violation [" + violations + "] to " + checkstyleViolations);
        String existingResults = (String) getResults().get(checkstyleViolations);
        
        long newViolation = violations;
        if (existingResults != null) 
        {
            newViolation = Long.parseLong(existingResults) + violations;
        }
        
        getResults().put(checkstyleViolations, Long.toString( newViolation ));
    }

    private void merge(String checkstyleTopViolations, String csv)
    {
        String oldTopViolations = getResults().put(checkstyleTopViolations, csv);
        String newTopViolations = csv;
        
        if (oldTopViolations != null) {
            Map<String, Integer> oldTopViolationsMap = CsvHelper.extractToCsv(oldTopViolations);
            Map<String, Integer> newTopViolationsMap = CsvHelper.extractToCsv(newTopViolations);
           
            HashMap<String , Integer> violationsPerFile = new HashMap<String, Integer>();
            violationsPerFile.putAll( oldTopViolationsMap );
            violationsPerFile.putAll( newTopViolationsMap );
            
            newTopViolations = CsvHelper.convertTopViolationsToCsv( violationsPerFile );
        }
        
        getResults().put(checkstyleTopViolations, newTopViolations);
    }

    /**
     * @return the results
     */
    public Map<String, String> getResults()
    {
        return results;
    }
}