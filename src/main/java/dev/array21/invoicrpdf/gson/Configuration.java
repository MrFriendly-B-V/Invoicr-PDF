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
import dev.array21.invoicrpdf.annotations.JsonRequired;
import dev.array21.invoicrpdf.annotations.Nullable;
import dev.array21.invoicrpdf.util.Utils;

public class Configuration {
	@JsonRequired
	Template[] templates;
	
	@JsonRequired
	String[] apiKeys;
	
	@Nullable
	public Template getTemplate(String name) {
		for(Template t : this.templates) {
			if(t.templateName.equalsIgnoreCase(name)) {
				return t;
			}
		}
		
		return null;
	}
	
	public List<String> getApiKeys() {
		return Arrays.asList(this.apiKeys);
	}
	
	public class Template {
		@JsonRequired
		public String templateName;
		
		@JsonRequired
		public String companyName;
		
		@JsonRequired
		public String logoPath;
		
		@JsonRequired
		public String outDir;
		
		@JsonRequired
		public String langPacks;
		
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
