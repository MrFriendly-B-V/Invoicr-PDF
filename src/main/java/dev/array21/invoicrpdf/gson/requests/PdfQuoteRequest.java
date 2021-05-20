package dev.array21.invoicrpdf.gson.requests;

import dev.array21.invoicrpdf.annotations.Nullable;
import dev.array21.invoicrpdf.annotations.Required;

public class PdfQuoteRequest extends PdfCommonRequest {
	
	/**
	 * The topic of the quote
	 */
	@Nullable
	public String quoteTopic;
	
	/**
	 * The representative from the company responsible for the quote
	 */
	@Required
	public String quoteContactPerson;
	
	/**
	 * The ID of the debit post creatd in the financial administration
	 */
	@Required
	public String debitId;
}
