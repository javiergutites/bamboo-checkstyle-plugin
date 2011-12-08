package com.atlassian.bamboo.plugins.checkstyle;

import java.io.File;

import junit.framework.TestCase;

public class CheckstylePluginHelperTest
    extends TestCase
{
    public void testValidHttpURLOK()
    {
        String urlToTest = "http://maven.apache.org/plugins/maven-checkstyle-plugin/";
        assertNull( CheckstylePluginHelper.validHttpURL( urlToTest ) );
    }

    public void testValidHttpURL404()
    {
        String urlToTest = "http://repository.atlassian.com/URLnotExist";
        String actualResult = CheckstylePluginHelper.validHttpURL( urlToTest );
        assertNotNull( actualResult );
        assertTrue( actualResult.contains( "#sec10.4.5" ) );
    }

    public void testValidHttpURLKO()
    {
        String urlToTest = "http://hostNotExist/URLnotExist";
        String actualResult = CheckstylePluginHelper.validHttpURL( urlToTest );
        assertNotNull( actualResult );
        assertEquals( "UnknownHostException: hostNotExist", actualResult );
    }

    public void testTransformFilenameInHttpURL()
    {
        String baseURL = "http://hostname:8080/projects/module/2.0-SNAPSHOT/";
        // common-util/checkstyle.html#com.sims.shared.util.reflect.ReflectionUtil.java
        String topViolationInitial =
            "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/projectName/src/main/java/com/company/util/reflect/ReflectionUtil.java,830"
                + CsvHelper.LINE_SEPARATOR
                + "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/projectName2/src/main/java/com/company/service/impl/GridServiceImpl.java,365"
                + CsvHelper.LINE_SEPARATOR
                + "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/projectName2/src/main/java/com/company/grid/util/FilterASTVisitor.java,305";

        String actual =
            CheckstylePluginHelper.transformFilenameInHttpURL(
                                                               new File(
                                                                         "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module" ),
                                                               baseURL, topViolationInitial );
        assertNotNull( actual );

        assertFalse( "Remove working directory failed",
                     actual.contains( "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/" ) );
        assertTrue( "base URL isn't added", actual.contains( baseURL ) );

        String expected =
            "http://hostname:8080/projects/module/2.0-SNAPSHOT/projectName/checkstyle.html#com.company.util.reflect.ReflectionUtil.java,830"
                + CsvHelper.LINE_SEPARATOR
                + "http://hostname:8080/projects/module/2.0-SNAPSHOT/projectName2/checkstyle.html#com.company.service.impl.GridServiceImpl.java,365"
                + CsvHelper.LINE_SEPARATOR
                + "http://hostname:8080/projects/module/2.0-SNAPSHOT/projectName2/checkstyle.html#com.company.grid.util.FilterASTVisitor.java,305"
                + CsvHelper.LINE_SEPARATOR;

        assertEquals( expected, actual );
    }

    public void testConvertFilenameInHttpURL()
    {
        String baseURL = "http://hostname:8080/projects/module/2.0-SNAPSHOT/";
        String filename =
            "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/projectName/src/main/java/com/company/util/reflect/ReflectionUtil.java";

        String actual =
            CheckstylePluginHelper.convertFilenameInHttpURL(
                                                             new File(
                                                                       "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module" ),
                                                             baseURL, filename );
        assertNotNull( actual );

        assertFalse( "Remove working directory failed : " + actual,
                     actual.contains( "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/" ) );
        assertEquals( baseURL + "projectName/checkstyle.html#com.company.util.reflect.ReflectionUtil.java", actual );
    }

    public void testConvertFilenameInHttpURLWithModuleInfilename()
    {
        String baseURL = "http://hostname:8080/projects/module/2.0-SNAPSHOT/";
        String filename =
            "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/module-api/src/main/java/com/company/util/reflect/ReflectionUtil.java";

        String actual =
            CheckstylePluginHelper.convertFilenameInHttpURL(
                                                             new File(
                                                                       "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module" ),
                                                             baseURL, filename );
        assertNotNull( actual );

        assertFalse( "Remove working directory failed : " + actual,
                     actual.contains( "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/" ) );
        assertEquals( baseURL + "module-api/checkstyle.html#com.company.util.reflect.ReflectionUtil.java", actual );
    }

    public void testConvertFilenameInHttpURLWithModuleInfilenameWithBaseURLNull()
    {
        String baseURL = null;
        String filename =
            "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/module-api/src/main/java/com/company/util/reflect/ReflectionUtil.java";

        String actual =
            CheckstylePluginHelper.convertFilenameInHttpURL(
                                                             new File(
                                                                       "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module" ),
                                                             baseURL, filename );
        assertNotNull( actual );

        assertFalse( "Remove working directory failed : " + actual,
                     actual.contains( "/bamboo-home/xml-data/build-dir/PROJECT-TRUNK/module/" ) );
        assertEquals( "/module-api/checkstyle.html#com.company.util.reflect.ReflectionUtil.java", actual );
    }
}
