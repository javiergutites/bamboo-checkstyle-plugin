/**
 *
 */
package com.atlassian.bamboo.plugins.checkstyle;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.io.StringReader;
import java.util.*;

/**
 * A simple helper classe for marshall and unmarshall the csv checkstyle result
 *
 * @author lauvigne
 */
public final class CsvHelper {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final int TOP_VIOLATIONS = 10;

    private CsvHelper() {
        throw new AssertionError("CsvHelper should not be instantiated");
    }

    /**
     * Convert the 10 first violation per file in csv result.
     *
     * @param violationsPerFile the map contains <filename, numberOfViolations>
     * @return the csv result
     */
    public static String convertTopViolationsToCsv(@Nonnull Map<String, Integer> violationsPerFile) {
        StringBuilder builder = new StringBuilder();
        List<CheckStyleViolationInformation> violations = Lists.newArrayList();
        for (String key : violationsPerFile.keySet()) {
            violations.add(new CheckStyleViolationInformation(key, violationsPerFile.get(key)));
        }

        Collections.sort(violations);

        // Append the 10 first Elements
        int max = violations.size() > TOP_VIOLATIONS ? TOP_VIOLATIONS : violations.size();
        for (int i = 0; (i < max); i++ )
        {
            CheckStyleViolationInformation info = violations.get(i);
            builder.append(info.getFileName()).append(",");
            builder.append(info.getNumberOfViolations()).append( LINE_SEPARATOR );
        }

        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> extractToCsv(final String csv) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        Iterator<String> iter = IOUtils.lineIterator( new StringReader( csv ) );
        while ( iter.hasNext() )
        {
            String[] keyValue = StringUtils.split( iter.next(), "," );
            result.put( keyValue[0], Integer.parseInt( keyValue[1] ) );
        }

        return result;
    }

}
