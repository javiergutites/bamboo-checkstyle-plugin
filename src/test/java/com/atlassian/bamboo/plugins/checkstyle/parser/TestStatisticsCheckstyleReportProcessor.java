package com.atlassian.bamboo.plugins.checkstyle.parser;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class TestStatisticsCheckstyleReportProcessor {

    private InputStream testFileStream;

    @After
    public void closeStream() {
        IOUtils.closeQuietly(testFileStream);
    }

    @Test
    public void testParsingSimpleFile() throws Exception {
        testFileStream = getClass().getResourceAsStream("/checkstyle/checkstyle-result-simple.xml");
        StatisticsCheckstyleResultProcessor processor = parseUsingProcessor();

        assertEquals(7, processor.getTotalViolations());
        assertEquals(4, processor.getErrorPriorityViolations());
        assertEquals(2, processor.getWarningPriorityViolations());
        assertEquals(1, processor.getInfoPriorityViolations());
        assertThat(processor.getViolationsPerFile(), Matchers.allOf(
                Matchers.hasEntry("/ClassOne.java", 2),
                Matchers.hasEntry("/ClassThree.java", 5)
        ));
    }

    @Test
    public void testParsingLongFile() throws Exception {
        testFileStream = getClass().getResourceAsStream("/checkstyle/checkstyle-result-long.xml");
        StatisticsCheckstyleResultProcessor processor = parseUsingProcessor();

        // just assert for the general counts
        assertEquals(963, processor.getTotalViolations());
        assertEquals(764, processor.getErrorPriorityViolations());
        assertEquals(185, processor.getWarningPriorityViolations());
        assertEquals(14, processor.getInfoPriorityViolations());

        long totalViolationsAggregated = processor.getErrorPriorityViolations()
                + processor.getWarningPriorityViolations()
                + processor.getInfoPriorityViolations();
        assertEquals(processor.getTotalViolations(), totalViolationsAggregated);
    }

    private StatisticsCheckstyleResultProcessor parseUsingProcessor() throws DocumentException {
        StatisticsCheckstyleResultProcessor processor = new StatisticsCheckstyleResultProcessor();
        new CheckStyleReportParser(processor).parse(testFileStream);
        return processor;
    }
}
