package dev.array21.invoicrpdf.gson;

import dev.array21.invoicrpdf.annotations.JsonRequired;
import dev.array21.invoicrpdf.annotations.Nullable;

public strictfp class PdfInvoiceRequest extends AuthenticatedRequest {
	
	@JsonRequired
	///The template to use for the invoice
	public String invoiceTemplateName;
	
	@JsonRequired
	public String language;
	
	///The ID the invoice should have
	@JsonRequired
	public String invoiceId;
	
	///The name of the person/organization to which the Invoice is addressed
	@JsonRequired
	public String receiver;
	
	///The reference of the invoice
	@JsonRequired
	public String reference;
	
	///For which administration in a company is this invoice
	@Nullable
	public String attentionOf;
	
	///Notes that should be attached to the bottom of the invoice
	@Nullable
	public String notes;
	
	///Discount percentage over the total order. Range 0-100%
	@Nullable
	public Float discountPerc;
	
	///The expiration date in Unix time
	@JsonRequired
	public Long expDate;
	
	///The date the invoice was created in Unix time
	@JsonRequired
	public Long invoiceDate;
	
	///The products to be displayed on the invoice
	@JsonRequired
	public RequestRow[] rows;

	///The billing address to be displayed on the invoice
	@JsonRequired
	public Address address;
	
	public boolean hasRowDiscount() {
		for(RequestRow row : this.rows) {
			if(row.discountPerc != null && row.discountPerc != 0f) {
				return true;
			}
		}
		
		return false;
	}
	
	///Class describing the structure of a product on an invoice
	public strictfp class RequestRow {
		///Any comment to be displayed under the product on the invoice
		@Nullable
		public String comment;
		
		///The ID of the product
		@JsonRequired
		public String id;
		
		///The name of the product
		@JsonRequired
		public String name;
		
		///Short description of the product
		@JsonRequired
		public String description;
		
		///Discount to be applied to this item only
		@Nullable
		public Float discountPerc;
		
		///The VAT percentage. Range 0-100%
		@JsonRequired
		public Float vatPerc;
		
		///The price of the product, excluding VAT
		@JsonRequired
		public Float price;
		
		///The amount of items to be invoiced
		@JsonRequired
		public Long quantity;
	}

	///Class describing the structure of the address on an invoice
	public class Address {
		@JsonRequired
		public String city;
		
		@JsonRequired
		public String country;
		
		@JsonRequired
		public String postalCode;
		
		@JsonRequired
		public String street;
	}
}