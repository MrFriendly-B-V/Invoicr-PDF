package dev.array21.invoicrpdf;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import dev.array21.invoicrpdf.gson.Configuration;
import dev.array21.invoicrpdf.util.Utils;

public class ConfigurationReader {
	
	public Configuration read(String configPath) {
		File configFile = new File(configPath);
		if(!configFile.getParentFile().exists()) {
			InvoicrPdf.logErr("Configuration parent folder does not exist: " + configFile.getParent());
			System.exit(1);
		}
		
		if(!configFile.exists()) {
			InvoicrPdf.logErr("Configuration file does not exist. (Creating)");
			
			try {
				InputStream in = ConfigurationReader.class.getResourceAsStream("/config.yml");
				if(in == null) {
					InvoicrPdf.logErr("Failed to find config.yml inside JARfile");
					System.exit(1);
				}
				
				Files.copy(in, configFile.toPath());
			} catch(IOException e) {
				InvoicrPdf.logErr("Failed to copy configuration file from JAR: " + e.getMessage());
				InvoicrPdf.logDebug(Utils.getStackTrace(e));
				System.exit(1);
			}
			
			InvoicrPdf.logInfo("Configuration file saved at " + configPath);
			InvoicrPdf.logInfo("Please configure Invoicr-PDF and then restart.");
			System.exit(0);
		}
		
		final Yaml yaml = new Yaml();
		final Gson gson = new Gson();
		
		Object configYaml;
		try {
			configYaml = yaml.load(new FileReader(configFile));
		} catch(IOException e) {
			InvoicrPdf.logErr("Failed to load configuration file: " + e.getMessage());
			InvoicrPdf.logDebug(Utils.getStackTrace(e));
			System.exit(1);
			return null;
		}
		
		String json = gson.toJson(configYaml, java.util.LinkedHashMap.class);
		Configuration config = gson.fromJson(json, Configuration.class);
		
		Pair<Boolean, String> validationResult = Utils.validateType(config);
		if(validationResult.getA() == null) {
			InvoicrPdf.logErr("Failed to validate Configuration: " + validationResult.getB());
			System.exit(1);
		}
		
		if(!validationResult.getA()) {
			InvoicrPdf.logErr("Invalid configuration file: " + validationResult.getB());
			System.exit(1);
		}
		
		InvoicrPdf.logInfo("Configuration file passed validation check.");
		return config;
	}
}
