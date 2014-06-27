package com.atlassian.bamboo.plugins.checkstyle.parser;

import com.atlassian.stash.codeanalysis.rest.CodeAnalysisNotification;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class TestDetailedJsonCheckstyleReportProcessor {

    private InputStream testFileStream;

    @After
    public void closeStream() {
        IOUtils.closeQuietly(testFileStream);
    }

    @Test
    public void testParsingSimpleFile() throws Exception {
        testFileStream = getClass().getResourceAsStream("/checkstyle/checkstyle-result-simple.xml");
        DetailedJsonCheckstyleResultProcessor processor = parseUsingProcessor();

        assertEquals(2, processor.getNotifications().size());
        assertThat(processor.getNotifications(), Matchers.allOf(
                hasEntry(is("/ClassOne.java"), Matchers.<CodeAnalysisNotification>iterableWithSize(2)),
                hasEntry(is("/ClassThree.java"), Matchers.<CodeAnalysisNotification>iterableWithSize(5))
        ));
    }

    private DetailedJsonCheckstyleResultProcessor parseUsingProcessor() throws DocumentException {
        DetailedJsonCheckstyleResultProcessor processor = new DetailedJsonCheckstyleResultProcessor();
        new CheckStyleReportParser(processor).parse(testFileStream);
        return processor;
    }
}
