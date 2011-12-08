package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import com.atlassian.bamboo.v2.build.CurrentBuildResult;

/**
 * CheckStyleInformation is a simple container for retrieve the checkstyle result.
 * 
 * @author lauvigne
 */
public class CheckStyleInformation
{
    /** Store checkstyle result */
    private Map<String, String> result;

    /**
     * CheckStyleViolationInformation.
     * 
     * @param customData the map that store checkstyle result
     */
    public CheckStyleInformation( Map<String, String> customData )
    {
        this.result = customData;
    }

    /**
     * CheckStyleViolationInformation.
     * 
     * @param buildResult the result that contains the map that store checkstyle result
     */
    public CheckStyleInformation( CurrentBuildResult buildResult )
    {
        this.result = buildResult.getCustomBuildData();
    }

    /**
     * @param key whose associated value is to be returned.
     * @return the Integer value to which this map maps the specified key, or
     *         <tt>0</tt> if the map contains no mapping for this key.
     */
    public Integer getInteger(String key)
    {
        Integer result = 0;
        String stringResult = getCustomdata().get( key );
        if (stringResult != null) 
        {
            result = Integer.decode( stringResult );  
        }
        
        return result;
    }
    
    /**
     * @return checkstyle top violations
     */
    public String getTopViolations()
    {
        return getCustomdata().get( ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS );
    }

    /**
     * @param numberViolations top violations to set
     */
    public void setTopViolations( String topViolations )
    {
        getCustomdata().put( ICheckStyleBuildProcessor.CHECKSTYLE_TOP_VIOLATIONS, topViolations );
    }

    /**
     * @return the number of checkstyle total violations
     */
    public Integer getTotalViolations()
    {
        return getInteger( ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATIONS );
    }

    /**
     * @param numberViolations the number of total violations to set
     */
    public void setTotalViolations( Integer numberViolations )
    {
        getCustomdata().put( ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATIONS, Integer.toString( numberViolations ) );
    }

    /**
     * @return the number of checkstyle Error violations
     */
    public Integer getErrorViolations()
    {
        return getInteger( ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS );
    }

    /**
     * @param numberViolations the number of Error violations to set
     */
    public void setErrorViolations( Integer numberViolations )
    {
        getCustomdata().put( ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_PRIORITY_VIOLATIONS,
                             Integer.toString( numberViolations ) );
    }

    /**
     * @return the number of checkstyle Warning violations
     */
    public Integer getWarningViolations()
    {
        return getInteger( ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS );
    }

    /**
     * @param numberViolations the number of Warning violations to set
     */
    public void setWarningViolations( Integer numberViolations )
    {
        getCustomdata().put( ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_PRIORITY_VIOLATIONS,
                             Integer.toString( numberViolations ) );
    }

    /**
     * @return the number of checkstyle Info violations
     */
    public Integer getInfoViolations()
    {
        return getInteger( ICheckStyleBuildProcessor.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS );
    }

    /**
     * @param numberViolations the number of Info violations to set
     */
    public void setInfoViolations( Integer numberViolations )
    {
        getCustomdata().put( ICheckStyleBuildProcessor.CHECKSTYLE_INFO_PRIORITY_VIOLATIONS,
                             Integer.toString( numberViolations ) );
    }

    /**
     * @return the result
     */
    public Map<String, String> getCustomdata()
    {
        return result;
    }

    /**
     * put in the customData Map
     * 
     * @param key
     * @param delta
     */
    private void put( String key, Integer delta )
    {
        getCustomdata().put( key, Integer.toString( delta ) );
    }

    /**
     * Set the total delta violation
     * @param previous the checkstyle information of previous build
     */
    public void setDeltaTotalViolations( CheckStyleInformation previous )
    {
        put( ICheckStyleBuildProcessor.CHECKSTYLE_TOTAL_VIOLATION_DELTA, getTotalViolations()
            - previous.getTotalViolations() );
    }
    /**
     * Set the Error delta violation
     * @param previous the checkstyle information of previous build
     */
    public void setDeltaErrorViolations( CheckStyleInformation previous )
    {
        put( ICheckStyleBuildProcessor.CHECKSTYLE_ERROR_VIOLATION_DELTA, getErrorViolations()
            - previous.getErrorViolations() );
    }
    /**
     * Set the Warning delta violation
     * @param previous the checkstyle information of previous build
     */
    public void setDeltaWarningViolations( CheckStyleInformation previous )
    {
        put( ICheckStyleBuildProcessor.CHECKSTYLE_WARNING_VIOLATION_DELTA, getWarningViolations()
            - previous.getWarningViolations() );
    }
    /**
     * Set the Info delta violation
     * @param previous the checkstyle information of previous build
     */
    public void setDeltaInfoViolations( CheckStyleInformation previous )
    {
        put( ICheckStyleBuildProcessor.CHECKSTYLE_INFO_VIOLATION_DELTA, getInfoViolations()
            - previous.getInfoViolations() );
    }
}