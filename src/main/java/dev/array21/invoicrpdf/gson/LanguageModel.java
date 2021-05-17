package dev.array21.invoicrpdf.gson;

import dev.array21.invoicrpdf.annotations.JsonRequired;

/// Class describing the layout of the language model
public class LanguageModel {
	///Title of the invoice, displayed in capital bold letters
	@JsonRequired
	public String invoiceHeader;
	
	///Reference header
	@JsonRequired
	public String reference;
	
	///Invoice Date header
	@JsonRequired
	public String invoiceDate;
	
	///Expiry date header
	@JsonRequired
	public String expiryDate;
	
	///The ID of the invoice header
	@JsonRequired
	public String invoiceId;
	
	///Article/Product ID header
	@JsonRequired
	public String productId;
	
	///Description of the product header
	@JsonRequired
	public String productDescription;
	
	///The amount of the product header
	@JsonRequired
	public String productQuantity;
	
	///The price of the product header
	@JsonRequired
	public String productPrice;
	
	///Optional product discount header. this isn't displayed if discount is 0.0
	@JsonRequired
	public String productDiscount;
	
	///The total price for the individual product with discount applied header. Not shown if discount is 0.0
	@JsonRequired
	public String productAmount;
	
	///The vat percentage header applied to the product
	@JsonRequired
	public String productVat;
	
	///The total price for the product * discount * quantity header. If discount is 0.0, Price is used 
	@JsonRequired
	public String productTotal;
	
	///Discount applied over the entire invoice header
	@JsonRequired
	public String totalDiscount;
	
	///The total price excluding VAT header
	@JsonRequired
	public String totalExVat;
	
	///The vat header
	@JsonRequired
	public String totalVat;
	
	///Total price header
	@JsonRequired
	public String totalPrice;
}
