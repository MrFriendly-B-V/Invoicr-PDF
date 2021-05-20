package dev.array21.invoicrpdf.gson.partials;

import dev.array21.invoicrpdf.annotations.Nullable;
import dev.array21.invoicrpdf.annotations.Required;

/**Class describing the structure of a product on an invoice or quote*/
public strictfp class ItemRow {
	/**Any comment to be displayed under the product on the invoice*/
	@Nullable
	public String comment;
	
	/**The ID of the product*/
	@Required
	public String id;
	
	/**The name of the product*/
	@Required
	public String name;
	
	/**Short description of the product*/
	@Required
	public String description;
	
	/**Discount to be applied to this item only*/
	@Nullable
	public Float discountPerc;
	
	/**The VAT percentage. Range 0-100%*/
	@Required
	public Float vatPerc;
	
	/**The price of the product, excluding VAT*/
	@Required
	public Float price;
	
	/**The amount of items to be invoiced*/
	@Required
	public Long quantity;
}
