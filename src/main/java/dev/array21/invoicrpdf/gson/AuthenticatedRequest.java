package dev.array21.invoicrpdf.gson;

import dev.array21.invoicrpdf.annotations.JsonRequired;

public abstract class AuthenticatedRequest {
	@JsonRequired
	public String apiKey;
}
