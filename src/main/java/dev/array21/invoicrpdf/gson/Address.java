package dev.array21.invoicrpdf.gson;

import dev.array21.invoicrpdf.annotations.Required;

/**Class describing the structure of the address on an invoice*/
public class Address {
	@Required
	public String city;
	
	@Required
	public String country;
	
	@Required
	public String postalCode;
	
	@Required
	public String street;
}