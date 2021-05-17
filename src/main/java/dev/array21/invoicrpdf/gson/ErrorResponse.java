package dev.array21.invoicrpdf.gson;

public class ErrorResponse {
	@SuppressWarnings("unused")
	private String error;
	
	public ErrorResponse(String error) {
		this.error = error;
	}
}
