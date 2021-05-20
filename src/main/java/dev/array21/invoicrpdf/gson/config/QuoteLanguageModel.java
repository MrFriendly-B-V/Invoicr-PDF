package dev.array21.invoicrpdf.gson.config;

import dev.array21.invoicrpdf.annotations.Required;

public class QuoteLanguageModel extends LanguageModel {

	/**
	 * The header at the top of the quote. E.g QUOTE
	 */
	@Required
	public String quoteHeader;
	
	/**
	 * The ID of the quote
	 */
	@Required
	public String quoteId;
	
	/**
	 * The date the quote was created
	 */
	@Required
	public String quoteDate;
	
	/**
	 * The date the quote expires
	 */
	@Required
	public String quoteExpiryDate;
	
	/**
	 * The representative from the company
	 */
	@Required
	public String contactPerson;
	
	/**
	 * Debit ID
	 */
	@Required
	public 	String debitId;
	
	/**
	 * Reference, this field is usually filled in by the customer
	 */
	@Required
	public 	String reference;
	
	/**
	 * Topic of the quote
	 */
	@Required
	public String topic;
	
	/**
	 * ID of the product
	 */
	@Required
	public String productId;
	
	/**
	 * Short product description
	 */
	@Required
	public String productDescription;
	
	/**
	 * Product quantity
	 */
	@Required
	public String productQuantity;
	
	/**
	 * The price of a single unit of the product, excluding VAT or a potential discount
	 */
	@Required
	public String productPrice;
	
	/**
	 * Discount in percentages. This field is ommited if the percentage is 0.0
	 */
	@Required
	public String productDiscount;
	
	/**
	 * The price of a single unit of the product, excluding VAT, but with the discount applied.
	 * This field is ommitted if the discount percentage is 0.0
	 */
	@Required
	public String productAmount;
	
	/**
	 * The VAT percentage for the product. This field is not ommitted if the VAT is 0.0
	 */
	@Required
	public String productVat;
	
	/**
	 * The total price for the product: Unit Price * Quantity. Without VAT, including discount (if applicable)
	 */
	@Required
	public String productTotal;
	
	/**
	 * Total quote price excluding VAT
	 */
	@Required
	public 	String totalExclVat;
	
	/**
	 * Total VAT for every item on the quote
	 */
	@Required
	public String totalVat;
	
	/**
	 * The total quote price including VAT
	 */
	@Required
	public String totalInclVat;
}
