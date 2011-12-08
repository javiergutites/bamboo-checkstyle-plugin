package com.atlassian.bamboo.plugins.checkstyle;

import java.util.Map;

import junit.framework.TestCase;

public class CsvHelperTest
    extends TestCase
{
    public final void testConvertTopViolationsToCsv()
    {
        Map<String, Integer> map = CsvHelper.extractToCsv( ICvsHelperConst.SAMPLE_TOP_VIOLATIONS );
        assertEquals(ICvsHelperConst.SAMPLE_TOP_VIOLATIONS, CsvHelper.convertTopViolationsToCsv( map ));
    }

    public final void testExtractToCsv()
    {
        Map<String, Integer> map = CsvHelper.extractToCsv( ICvsHelperConst.SAMPLE_TOP_VIOLATIONS );
        assertEqualsIntInt( 365, map.get( "/ProjectPath/src/main/java/packagename/grid/service/impl/GridServiceImpl.java" ) );
        assertEqualsIntInt( 295, map.get( "/ProjectPath/src/main/java/packagename/grid/domain/model/impl/Grid.java" ) );
        assertEqualsIntInt( 173, map.get( "/ProjectPath/src/main/java/packagename/grid/domain/dao/jpa/GridDAO.java" ) );
        assertEqualsIntInt( 63, map.get( "/ProjectPath/src/main/java/packagename/grid/service/impl/FieldRepositoryImpl.java" ) );
        assertEqualsIntInt( 41, map.get( "/ProjectPath/src/main/java/packagename/grid/domain/model/impl/GridColumn.java" ) );
        assertEqualsIntInt( 26, map.get( "/ProjectPath/src/main/java/packagename/grid/domain/dao/IGridDAO.java" ) );
    }

    private void assertEqualsIntInt( Integer i, Integer integer )
    {
        assertEquals( i, integer );
    }
}