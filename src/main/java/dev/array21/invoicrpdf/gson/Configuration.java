package dev.array21.invoicrpdf.gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import dev.array21.invoicrpdf.InvoicrPdf;
import dev.array21.invoicrpdf.Pair;
import dev.array21.invoicrpdf.annotations.Required;
import dev.array21.invoicrpdf.annotations.Nullable;
import dev.array21.invoicrpdf.util.Utils;

public class Configuration {
	/**Configured templates*/
	@Required
	public Template[] templates;
	
	@Required
	public HmacKey[] hmacKeys;
	
	/**The directory to write generated PDFs too*/
	@Required
	public String outDir;
	
	/**
	 * Get a template
	 * @param name The name of the template
	 * @return Returns the template, or null if it doesn't exist
	 */
	@Nullable
	public Template getTemplate(String name) {
		for(Template t : this.templates) {
			if(t.templateName.equalsIgnoreCase(name)) {
				return t;
			}
		}
		
		return null;
	}
	
	/**
	 * Get all HMAC keys as a List
	 * @return Returns all configured HMAC keys
	 */
	public List<HmacKey> getHmacKeys() {
		return Arrays.asList(this.hmacKeys);
	}
	
	public class HmacKey {
		@Required
		public String key;
		
		@Required
		public String secret;
	}
	
	/**
	 * Configuration template
	 */
	public class Template {
		
		/**The name of the template*/
		@Required
		public String templateName;
		
		/**Full path to the logo to be used in this template. On Windows double backslashes should be used*/
		@Required
		public String logoPath;
		
		/**Folder in which the language files for this template reside. This may be shared with other templates too*/
		@Required
		public String langPacks;
		
		/**
		 * Get a language model from the template
		 * @param language The name of the language model
		 * @return Returns the requested model, or null if it doesn't exist, or if it doesn't pass validation
		 */
		@Nullable
		public LanguageModel getLanguageModel(String language) {
			File modelFile = new File(this.langPacks, language + ".yml");
			if(!modelFile.exists()) {
				InvoicrPdf.logErr("Tried opening nonexistent language model " + modelFile.getAbsolutePath());
				return null;
			}
			
			final Gson gson = new Gson();
			final Yaml yaml = new Yaml();
			
			Object modelYaml;
			try {
				modelYaml = yaml.load(new FileReader(modelFile));
			} catch (FileNotFoundException e) {
				//Cant happen due to the check above
				return null;
			}
			
			String modelJson = gson.toJson(modelYaml, java.util.LinkedHashMap.class);
			LanguageModel model = gson.fromJson(modelJson, LanguageModel.class);
			
			Pair<Boolean, String> validationResult = Utils.validateType(model);
			if(validationResult.getA() == null) {
				return null;
			}
			
			if(!validationResult.getA()) {
				InvoicrPdf.logErr(String.format("Language model '%s' from Template '%s' failed validation: %s", language, this.templateName, validationResult.getB()));
				return null;
			}
			
			return model;
		}
	}
}
