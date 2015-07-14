package com.joshcummings.ws.auction;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class ConfigurationFactory {
	  
    private volatile static Properties configProperties;
    public static final String propertiesFilePath="application.properties";
    public synchronized static Properties getProperties() {

        if(configProperties==null) {
            configProperties=new Properties();
            try {
                configProperties.load(new FileInputStream(propertiesFilePath));
            } catch (IOException ex) {
                Logger.getLogger(ConfigurationFactory.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }

        }

        return configProperties;
    }

    
    

    public @Produces @Config String getConfiguration(InjectionPoint p) {

        String configKey=p.getMember().getDeclaringClass().getName()+"."+p.getMember().getName();
        Properties config=getProperties();
        if(config.getProperty(configKey)==null) {
            configKey=p.getMember().getDeclaringClass().getSimpleName()+"."+p.getMember().getName();
            if(config.getProperty(configKey)==null)
                configKey=p.getMember().getName();
        }
        System.err.println("Config key= "+configKey+" value = "+config.getProperty(configKey));

        return config.getProperty(configKey);
    }     
}
