package com.atlassian.bamboo.plugins.checkstyle.tasks;

import com.atlassian.bamboo.plugins.checkstyle.CsvHelper;
import com.atlassian.bamboo.plugins.checkstyle.parser.CheckStyleReportParser;
import com.atlassian.bamboo.plugins.checkstyle.parser.CheckstyleResultProcessor;
import com.atlassian.bamboo.plugins.checkstyle.parser.StatisticsCheckstyleResultProcessor;
import com.atlassian.bamboo.utils.FileVisitor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collections;
import java.util.Map;

import static com.atlassian.bamboo.plugins.checkstyle.CheckStyleBambooConstants.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

public class CheckStyleFileVisitor extends FileVisitor {

    private static final Logger log = Logger.getLogger(CheckStyleFileVisitor.class);

    private final File root;
    private final Map<String, String> results;
    private final Map<String, Integer> violationsPerFile = Maps.newHashMap();

    private final Iterable<CheckstyleResultProcessor> processors;

    public CheckStyleFileVisitor(File root, Map<String, String> results) {
        this(root, results, Collections.<CheckstyleResultProcessor>emptyList());
    }

    public CheckStyleFileVisitor(@Nonnull File root, @Nonnull Map<String, String> results,
                                 @Nonnull Iterable<CheckstyleResultProcessor> customProcessors) {
        super(checkNotNull(root, "root"));
        this.root = root;
        this.results = checkNotNull(results, "results");
        this.processors = ImmutableList.copyOf(customProcessors);
    }

    public void visitFile(File file) {
        if (file.getName().endsWith( "xml" ))
        {
            runParse(file);
        }
    }

    public CheckStyleFileVisitor withProcessors(@Nonnull Iterable<CheckstyleResultProcessor> moreProcessors) {
            return new CheckStyleFileVisitor(root, results, Iterables.concat(processors, moreProcessors));
        }

    public CheckStyleFileVisitor withProcessors(@Nonnull CheckstyleResultProcessor... moreProcessors) {
        return withProcessors(asList(moreProcessors));
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