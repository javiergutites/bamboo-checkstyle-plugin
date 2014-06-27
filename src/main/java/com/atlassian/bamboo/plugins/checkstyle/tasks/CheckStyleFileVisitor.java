package com.atlassian.bamboo.plugins.checkstyle.tasks;

import com.atlassian.bamboo.plugins.checkstyle.CsvHelper;
import com.atlassian.bamboo.plugins.checkstyle.ICheckStyleBuildProcessor;
import com.atlassian.bamboo.plugins.checkstyle.parser.CheckStyleReportParser;
import com.atlassian.bamboo.plugins.checkstyle.parser.StatisticsCheckstyleResultProcessor;
import com.atlassian.bamboo.utils.FileVisitor;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class CheckStyleFileVisitor extends FileVisitor implements ICheckStyleBuildProcessor {

    private static final Logger log = Logger.getLogger(CheckStyleFileVisitor.class);

    private final Map<String, String> results;
    private final Map<String, Integer> violationsPerFile = Maps.newHashMap();

    // TODO
//    private final Iterable<CheckstyleResultProcessor> moreProcessors;

    public CheckStyleFileVisitor(File file, Map<String, String> results) {
        super( file );
        this.results = results;
    }

    public void visitFile(File file) {
        if (file.getName().endsWith( "xml" ))
        {
            runParse(file);
        }
    }

    public void finished() {
        results.put(CHECKSTYLE_TOP_VIOLATIONS, CsvHelper.convertTopViolationsToCsv(violationsPerFile));
    }

    private void runParse(final File file)
    {
        try
        {
            log.info("Parsing file: " + file.getAbsolutePath());
            StatisticsCheckstyleResultProcessor statisticsProcessor = new StatisticsCheckstyleResultProcessor();
            new CheckStyleReportParser(statisticsProcessor).parse(file);

            mergeViolationsPerFile(statisticsProcessor.getViolationsPerFile());
            merge(CHECKSTYLE_TOTAL_VIOLATIONS, statisticsProcessor.getTotalViolations());
            merge(CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS, statisticsProcessor.getErrorPriorityViolations());
            merge(CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS, statisticsProcessor.getWarningPriorityViolations());
            merge(CHECKSTYLE_INFO_PRIORITY_VIOLATIONS, statisticsProcessor.getInfoPriorityViolations());
        }
        catch ( Exception e )
        {
            log.error( "Failed to parse artifact result file \"" + file.getName() + "\"", e );
        }
    }

    private void merge(String checkstyleViolations, long violations)
    {
        log.debug("Appending current violation [" + violations + "] to " + checkstyleViolations);
        String existingResults = getResults().get(checkstyleViolations);
        
        long newViolation = violations;
        if (existingResults != null) 
        {
            newViolation = Long.parseLong(existingResults) + violations;
        }
        
        getResults().put(checkstyleViolations, Long.toString( newViolation ));
    }

    private void mergeViolationsPerFile(Map<String, Integer> newViolationsPerFile) {
        // if various checks are over the same file, they will be overridden. This is how it behaved before and in
        // some ways it makes sense
        violationsPerFile.putAll(newViolationsPerFile);
    }

    /**
     * @return the results
     */
    public Map<String, String> getResults()
    {
        return results;
    }
}