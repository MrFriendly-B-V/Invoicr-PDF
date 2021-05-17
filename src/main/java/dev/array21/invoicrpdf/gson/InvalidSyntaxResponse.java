package dev.array21.invoicrpdf.gson;

public class InvalidSyntaxResponse {
	@SuppressWarnings("unused")
	private String error;
	
	public InvalidSyntaxResponse(String error) {
		this.error = error;
	}
}
