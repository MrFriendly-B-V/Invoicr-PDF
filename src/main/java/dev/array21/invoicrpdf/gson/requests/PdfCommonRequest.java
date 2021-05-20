package dev.array21.invoicrpdf.gson.requests;

import dev.array21.invoicrpdf.annotations.External;
import dev.array21.invoicrpdf.annotations.Nullable;
import dev.array21.invoicrpdf.annotations.Required;
import dev.array21.invoicrpdf.gson.partials.Address;
import dev.array21.invoicrpdf.gson.partials.ItemRow;

public class PdfCommonRequest {
	
	/**The template to use*/
	@Required
	public String templateName;
	
	/**The language in which the invoice or quote should be created */
	public String language;
	
	/**The ID the invoice or quote should have*/
	@Required
	public String id;
	
	/**For which administration in a company is this invoice*/
	@Nullable
	public String attentionOf;
	
	/**The name of the person/organization to which the invoice or quote is addressed*/
	@Required
	public String receiver;
	
	/**The reference of the invoice or quote */
	@Required
	public String reference;
	
	/**Notes that should be attached to the bottom of the invoice or quote */
	@Nullable
	public String notes;
	
	/**The expiration date in Unix time*/
	@Required
	public Long expiryDate;
	
	/**The date the invoice or order was created in Unix time*/
	@Required
	public Long creationDate;
	
	/**The products to be displayed on the invoice*/
	@Required
	@External
	public ItemRow[] rows;
	
	/**The billing address to be displayed on the invoice or quote */
	@Required
	@External
	public Address address;
	
	/**
	 * Check if any ItemRow has a discount
	 * @return Returns true if any of the ItemRow's hasa discount > 0.0f. Returns false if none of the ItemRow's do
	 */
	public boolean hasRowDiscount() {
		for(ItemRow row : this.rows) {
			if(row.discountPerc != null && row.discountPerc != 0f) {
				return true;
			}
		}
		
		return false;
	}
}
