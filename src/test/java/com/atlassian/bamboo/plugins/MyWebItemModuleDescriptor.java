package com.atlassian.bamboo.plugins;

import com.atlassian.plugin.web.DefaultWebInterfaceManager;
import com.atlassian.plugin.web.descriptors.DefaultWebItemModuleDescriptor;

/**
 * Class Just for fix a NPE 
 */
public class MyWebItemModuleDescriptor extends DefaultWebItemModuleDescriptor {
    public MyWebItemModuleDescriptor()
    {
        setWebInterfaceManager( new DefaultWebInterfaceManager()  );
    }
}
