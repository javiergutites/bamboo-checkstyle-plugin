package com.atlassian.bamboo.plugins.checkstyle;


/**
 * Constant common for test
 * @author lauvigne
 */
public interface ICvsHelperConst
{
    /** */
    String LINE_SEPARATOR = CsvHelper.LINE_SEPARATOR;
    /** */
    String SAMPLE_TOP_VIOLATIONS =
        "/ProjectPath/src/main/java/packagename/grid/service/impl/GridServiceImpl.java,365" + LINE_SEPARATOR
            + "/ProjectPath/src/main/java/packagename/grid/domain/model/impl/Grid.java,295" + LINE_SEPARATOR
            + "/ProjectPath/src/main/java/packagename/grid/domain/dao/jpa/GridDAO.java,173" + LINE_SEPARATOR
            + "/ProjectPath/src/main/java/packagename/grid/service/impl/FieldRepositoryImpl.java,63" + LINE_SEPARATOR
            + "/ProjectPath/src/main/java/packagename/grid/domain/model/impl/GridColumn.java,41" + LINE_SEPARATOR
            + "/ProjectPath/src/main/java/packagename/grid/domain/dao/IGridDAO.java,26" + LINE_SEPARATOR ;
}
