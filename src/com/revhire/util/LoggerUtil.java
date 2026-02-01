package com.revhire.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.File;

public class LoggerUtil {
    private static boolean initialized = false;
    
    public static void initialize() {
        if (!initialized) {
            try {
                // Look for log4j.properties in project root
                File log4jFile = new File("log4j.properties");
                if (log4jFile.exists()) {
                    PropertyConfigurator.configure(log4jFile.getAbsolutePath());
                    //System.out.println("Log4J configured from: " + log4jFile.getAbsolutePath());
                } else {
                    // Try classpath
                    PropertyConfigurator.configure(LoggerUtil.class.getClassLoader().getResource("log4j.properties"));
                    //System.out.println("Log4J configured from classpath");
                }
                initialized = true;
                Logger.getLogger(LoggerUtil.class).info("Log4J initialized successfully");
            } catch (Exception e) {
                System.err.println("Failed to initialize Log4J: " + e.getMessage());
                // Fallback to basic logging
                e.printStackTrace();
            }
        }
    }
    
    public static Logger getLogger(Class<?> clazz) {
        if (!initialized) {
            initialize();
        }
        return Logger.getLogger(clazz);
    }
}