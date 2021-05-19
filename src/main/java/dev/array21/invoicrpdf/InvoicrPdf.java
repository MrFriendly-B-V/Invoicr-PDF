package dev.array21.invoicrpdf;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dev.array21.invoicrpdf.gson.Configuration;

@SpringBootApplication
public class InvoicrPdf {
    
	private static final Logger LOGGER = LogManager.getLogger(InvoicrPdf.class);
	public static boolean DEBUG = false;
	public static boolean AUTH = true;
	private static Configuration config;
	
	public static void main(String[] args) {
		String port = "8080";
		String configPath = null;
		
		for(int i = 0; i < args.length; i++) {
			switch(args[i]) {
				case "--debug":
					DEBUG = true;
					break;
				case "--port":
					i++;
					if(args[1] == null) {
						logErr("Error: --port given, but no portnumber given.");
						System.exit(1);
					}
					port = args[1];
					
					break;
				case "--config":
					i++;
					configPath = args[i];
					break;
				case "--no-auth":
					AUTH = false;
					logErr("WARNING: You have disabled authentication. DO NOT use this in production!");
					break;
				default: 
					logErr("Invalid argument");
					break;
			}
		}
		
		if(configPath == null) {
			logErr("Error: Missing required argument '--config <PATH TO CONFIG>'");
			System.exit(1);
		}
		
		Configuration config = new ConfigurationReader().read(configPath);
		InvoicrPdf.config = config;
		
		logInfo("Starting on port " + port);
		Properties sysProps = System.getProperties();
		sysProps.setProperty("server.port", port);
		
		new InvoicrPdf().init();
    }
	
	private void init() {
		SpringApplication.run(InvoicrPdf.class);
	}
	
	public static Configuration getConfig() {
		return InvoicrPdf.config;
	}
	
	public static void logDebug(Object o) {
		if(DEBUG) {
			LOGGER.debug(o);
		}
	}
	
	public static void logErr(Object o) {
		LOGGER.error(o);
	}
	
	public static void logInfo(Object o) {
		LOGGER.info(o);
	}
}