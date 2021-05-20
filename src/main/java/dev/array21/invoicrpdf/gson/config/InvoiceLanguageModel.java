package dev.array21.invoicrpdf.gson.config;

import dev.array21.invoicrpdf.annotations.Required;

/** Class describing the layout of the language model*/
public class InvoiceLanguageModel extends LanguageModel {
	/**Title of the invoice, displayed in capital bold letters*/
	@Required
	public String invoiceHeader;
	
	/**Reference header*/
	@Required
	public String reference;
	
	/**Invoice Date header*/
	@Required
	public String invoiceDate;
	
	/**Expiry date header*/
	@Required
	public String expiryDate;
	
	/**The ID of the invoice header*/
	@Required
	public String invoiceId;
	
	/**Article/Product ID header*/
	@Required
	public String productId;
	
	/**Description of the product header*/
	@Required
	public String productDescription;
	
	/**The amount of the product header*/
	@Required
	public String productQuantity;
	
	/**The price of the product header*/
	@Required
	public String productPrice;
	
	/**Optional product discount header. this isn't displayed if discount is 0.0*/
	@Required
	public String productDiscount;
	
	/**The total price for the individual product with discount applied header. Not shown if discount is 0.0*/
	@Required
	public String productAmount;
	
	/**The vat percentage header applied to the product*/
	@Required
	public String productVat;
	
	/**The total price for the product * discount * quantity header. If discount is 0.0, Price is used */
	@Required
	public String productTotal;
	
	/**The total price excluding VAT header*/
	@Required
	public String totalExVat;
	
	/**The vat header*/
	@Required
	public String totalVat;
	
	/**Total price header*/
	@Required
	public String totalPrice;
}
