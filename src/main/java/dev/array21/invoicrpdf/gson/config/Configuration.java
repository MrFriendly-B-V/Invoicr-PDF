package dev.array21.invoicrpdf.gson.config;

import java.util.Arrays;
import java.util.List;

import dev.array21.invoicrpdf.annotations.Required;
import dev.array21.invoicrpdf.annotations.External;
import dev.array21.invoicrpdf.annotations.Nullable;

public class Configuration {
	/**Configured templates*/
	@Required
	@External
	public Template[] templates;
	
	@Required
	@External
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
}
