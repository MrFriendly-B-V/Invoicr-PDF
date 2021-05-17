package dev.array21.invoicrpdf.pdf;

import dev.array21.invoicrpdf.gson.PdfInvoiceRequest;

public class InvoiceGenerator {

	private final PdfInvoiceRequest body;
	
	public InvoiceGenerator(PdfInvoiceRequest body) {
		this.body = body;
	}
	
	public void generate(String fileId) {
		
	}
}
