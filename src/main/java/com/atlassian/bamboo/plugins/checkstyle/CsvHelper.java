/**
 * 
 */
package com.atlassian.bamboo.plugins.checkstyle;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A simple helper classe for marshall and unmarshall the csv checkstyle result
 * 
 * @author lauvigne
 */
public class CsvHelper
{
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    /**
     * Convert the 10 first violation per file in csv result.
     * 
     * @param violationsPerFile the map contains <filename, numberOfViolations>
     * @return the csv result
     */
    public static String convertTopViolationsToCsv( Map<String, Integer> violationsPerFile )
    {
        StringBuffer buffer = new StringBuffer();
        List<CheckStyleViolationInformation> violations = new ArrayList<CheckStyleViolationInformation>();

        // populate the List
        Iterator<String> keys = violationsPerFile.keySet().iterator();
        while ( keys.hasNext() )
        {
            String key = keys.next();
            int value = violationsPerFile.get( key ).intValue();
            violations.add( new CheckStyleViolationInformation( key, value ) );
        }

        // Sort it
        Collections.sort( violations );

        // Append the 10 first Elements
        for ( int i = 0; ( i < ( ( violations.size() > 10 ) ? 10 : violations.size() ) ); i++ )
        {
            CheckStyleViolationInformation info = violations.get( i );
            buffer.append( info.getFileName() ).append( "," );
            buffer.append( info.getNumberOfViolations() ).append( LINE_SEPARATOR );
        }

        return buffer.toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> extractToCsv( final String csv )
    {
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
