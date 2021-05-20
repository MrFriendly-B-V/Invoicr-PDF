package dev.array21.invoicrpdf.gson.requests;

import dev.array21.invoicrpdf.annotations.Required;

public class GetPdfRequest {

	/**
	 * The ID of the PDF to fetch
	 */
	@Required
	public String id;
	
	/**
	 * The type of PDF being requested
	 */
	@Required
	public PdfType type;
}
