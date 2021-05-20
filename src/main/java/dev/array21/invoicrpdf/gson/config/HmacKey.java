package dev.array21.invoicrpdf.gson.config;

import dev.array21.invoicrpdf.annotations.Required;

public class HmacKey {
	@Required
	public String key;
	
	@Required
	public String secret;
}