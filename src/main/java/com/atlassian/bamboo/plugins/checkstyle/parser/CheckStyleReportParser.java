package com.atlassian.bamboo.plugins.checkstyle.parser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.emory.mathcs.backport.java.util.Arrays.asList;

/**
 * CheckStyleReportParser.
 *
 * @author Stephan Paulicke
 */
public class CheckStyleReportParser {

    private static final String CHECKSTYLE_XPATH = "//checkstyle/file";
    private static final String ERROR_XPATH = "error";
    private static final String NAME_ATTR_XPATH = "@name";

    private final Iterable<CheckstyleResultProcessor> processors;

    public CheckStyleReportParser(@Nonnull CheckstyleResultProcessor... processors) {
        this(asList(processors));
    }

    public CheckStyleReportParser(@Nonnull Iterable<CheckstyleResultProcessor> processors) {
        this.processors = checkNotNull(processors, "processors");
    }

    /**
     * Parse an input stream with checkstyle results.
     *
     * @param stream stream with XML to parse
     */
    public void parse(@Nonnull InputStream stream) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(stream);
        parse(document);
    }

    /**
     * Parse a results file
     *
     * @param file file with XML checkstyle results to parse
     * @throws MalformedURLException
     * @throws DocumentException
     */
    public void parse(@Nonnull File file) throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        parse(document);
    }

    private void parse(Document document) {
        @SuppressWarnings("unchecked") List<Node> files = document.selectNodes(CHECKSTYLE_XPATH);

        for (Node fileNode : files) {
            @SuppressWarnings("unchecked") List<Node> errors = fileNode.selectNodes(ERROR_XPATH);
            String fileName = fileNode.valueOf("@name");

            for (Node errorNode : errors) {
                CheckstyleError error = CheckstyleError.fromNode(fileName, errorNode);
                for (CheckstyleResultProcessor processor : processors) {
                    processor.onError(error);
                }
            }
        }
    }

    /********************************************************************************************************
     * convertTopViolationsToCsv().
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
//    public String convertTopViolationsToCsv() throws FileNotFoundException {
//        return CsvHelper.convertTopViolationsToCsv(violationsPerFile);
//    }

}