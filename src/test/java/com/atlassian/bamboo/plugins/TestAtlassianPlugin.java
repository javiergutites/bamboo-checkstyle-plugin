package com.atlassian.bamboo.plugins;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.atlassian.bamboo.plugin.BambooModuleDescriptorFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.impl.StaticPlugin;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.XmlDescriptorParserFactory;

/**
 * @author lauvigne
 */
public class TestAtlassianPlugin
    extends TestCase
{
    public void testBasic()
    {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream( "atlassian-plugin.xml" );
        assertNotNull( in );

        XmlDescriptorParserFactory factory = new XmlDescriptorParserFactory();
        try
        {
            DescriptorParser parser = factory.getInstance( in );
            assertNotNull( parser );

            Plugin plugin = new StaticPlugin();
            assertNull( plugin.getKey() );
            assertNull( plugin.getName() );
            assertNull( plugin.getPluginInformation() );
            assertNull( plugin.getKey() );
            assertEquals( 0, plugin.getModuleDescriptors().size() );
            // Load xml file
            BambooModuleDescriptorFactory bambooModuleDescriptorFactory = new BambooModuleDescriptorFactory(null);
            //override the webitem descriptor for fix a NPE 
            bambooModuleDescriptorFactory.addModuleDescriptor( "web-item", MyWebItemModuleDescriptor.class );
            plugin = parser.configurePlugin( bambooModuleDescriptorFactory, plugin );

            // Assert just if is nullable, because value can change
            assertNotNull( plugin.getKey() );
            assertNotNull( plugin.getName() );
            assertNotNull( plugin.getPluginInformation() );
            assertNotNull( plugin.getKey() );
            assertEquals( 9, plugin.getModuleDescriptors().size() );
            ModuleDescriptor moduleDescriptor = plugin.getModuleDescriptor( "checkstyleBuildProcessor" );
            assertNotNull( moduleDescriptor );
            assertNotNull( moduleDescriptor.getDescription() );
            assertEquals( 2, moduleDescriptor.getResourceDescriptors().size() );

        }
        catch ( PluginParseException e )
        {
            fail( "atlassian-plugin.xml is not correct : see " + ExceptionUtils.getFullStackTrace( e ) );
        }
    }
}
