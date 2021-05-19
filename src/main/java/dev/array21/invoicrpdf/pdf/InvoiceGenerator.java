package dev.array21.invoicrpdf.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

import dev.array21.invoicrpdf.gson.Configuration.Template;
import dev.array21.invoicrpdf.gson.PdfInvoiceRequest.RequestRow;
import dev.array21.invoicrpdf.InvoicrPdf;
import dev.array21.invoicrpdf.gson.LanguageModel;
import dev.array21.invoicrpdf.gson.PdfInvoiceRequest;

public strictfp class InvoiceGenerator {

	private final PdfInvoiceRequest body;
	
	public InvoiceGenerator(PdfInvoiceRequest body) {
		this.body = body;
	}
	
	public strictfp void generate(String fileId) {
		Template template = InvoicrPdf.getConfig().getTemplate(this.body.invoiceTemplateName);
		LanguageModel languageModel = template.getLanguageModel(body.language);
		if(languageModel == null) {
			return;
		}

		//Simply, a black line
		SolidLine blackLine = new SolidLine(1f);
		blackLine.setColor(ColorConstants.BLACK);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-mm-YYYY");
		
		//Lets create the document
		PdfDocument pdf;
		try {
			pdf = new PdfDocument(new PdfWriter(new File(template.outDir, fileId + ".pdf")));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		Document doc = new Document(pdf);		
		doc.setMargins(40f, 30f, 40f, 30f);		
		
		//The addressing is at the top of the invoice, it describes for whom it's ment
		Paragraph addressing = new Paragraph();
		addressing.add(new Text(body.receiver + "\n").setBold());
		addressing.add(new Text(body.address.street + "\n"));
		addressing.add(new Text(body.address.postalCode + " " + body.address.city + "\n"));
		addressing.add(new Text(body.address.country));
		
		//We store both the heading, as well as the company logo in a table for alignment purposes
		Table heading = new Table(new float[] {1f, 1f});
		heading.startNewRow();
		heading.setHorizontalAlignment(HorizontalAlignment.CENTER);

		Cell addressingCell = new Cell();
		addressingCell.add(addressing);
		addressingCell.setWidth(UnitValue.createPercentValue(50f));
		addressingCell.setBorder(Border.NO_BORDER);
		heading.addCell(addressingCell);
		
		//Add te company logo
		try {
			Cell imageCell = new Cell();
			Image image = new Image(ImageDataFactory.create(template.logoPath));
			imageCell.add(image);
			imageCell.setBorder(Border.NO_BORDER);
			heading.addCell(imageCell);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
		doc.add(heading);
		
		//The invoiceheader is simple, it screams INVOICE (depending on the languageModel used)
		Paragraph invoiceHeader = new Paragraph(languageModel.invoiceHeader);
		invoiceHeader.setHorizontalAlignment(HorizontalAlignment.LEFT);
		invoiceHeader.setWidth(pdf.getDefaultPageSize().getWidth() - doc.getLeftMargin() - doc.getRightMargin());
		invoiceHeader.setTextAlignment(TextAlignment.RIGHT);
		invoiceHeader.setBold();
		invoiceHeader.setFontSize(30);		
		doc.add(invoiceHeader);

		//Add a black line to mark out the areas clearly
		doc.add(new LineSeparator(blackLine));
		
		//The reference table contains information about the invoice
		//- A customer-set reference
		//- Invoice date
		//- Expiry date
		//- Invoice ID
		//The first row are the headers
		Table referenceTable = new Table(new float[] {3, 1, 1, 1});
		referenceTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		referenceTable.useAllAvailableWidth();
		referenceTable.startNewRow();
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.reference).setBold()).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.invoiceDate).setBold()).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.expiryDate).setBold()).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.invoiceId).setBold()).setBorder(Border.NO_BORDER));
		
		//The second row are the values matching to the headers
		//First of all the reference, set by the customer
		referenceTable.startNewRow();
		referenceTable.addCell(new Cell().add(new Paragraph(body.reference)).setBorder(Border.NO_BORDER));
		
		//Invoice Expiry date
		Date expiryDate = new Date(body.expDate);
		referenceTable.addCell(new Cell().add(new Paragraph(dateFormatter.format(expiryDate))).setBorder(Border.NO_BORDER));
		
		//Invoice date
		Date invoiceDate = new Date(body.invoiceDate);
		referenceTable.addCell(new Cell().add(new Paragraph(dateFormatter.format(invoiceDate))).setBorder(Border.NO_BORDER));
		
		//Lastly, invoice ID
		referenceTable.addCell(new Cell().add(new Paragraph(body.invoiceId)).setBorder(Border.NO_BORDER));
		doc.add(referenceTable);

		//Add another black line
		doc.add(new LineSeparator(blackLine));
		
		//Check if we give a discount, thisll change the column layout of the products table
		//Since when there is no discount, we skip the Discount and Amount column
		boolean rowDiscount = body.hasRowDiscount();
		float[] requestRowLayout;
		if(rowDiscount) {
			requestRowLayout = new float[] { 1f, 2f, 1f, 1f, 1f, 1f, 1f, 1f };
		} else {
			requestRowLayout = new float[] { 1f, 2f, 1f, 1f, 1f, 1f};
		}
		
		//Now for the important part, the table containing all invoiced products
		//The first row will be the headers
		Table productsTable = new Table(requestRowLayout);
		productsTable.startNewRow();
		productsTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		productsTable.useAllAvailableWidth();
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productId).setBold()).setBorder(Border.NO_BORDER));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productDescription).setBold()).setBorder(Border.NO_BORDER));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productQuantity).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productPrice).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		
		//If a discount is given, add those headers too
		if(rowDiscount) {
			productsTable.addCell(new Cell().add(new Paragraph(languageModel.productDiscount).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
			productsTable.addCell(new Cell().add(new Paragraph(languageModel.productAmount).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		}
		
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productVat).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productTotal).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		
		//Now let's iterate over every product and add it to the table
		//We'll also calculate the totalPrice and totalVat while we're at it
		float totalPrice = 0f;
		float totalVat = 0f;
		for(RequestRow row : body.rows) {
			float totalProductPrice = 0f;
			if(rowDiscount) {
				totalProductPrice = row.price * (100f - row.discountPerc)/100f * (float) row.quantity;
			} else {
				totalProductPrice = row.price * (float) row.quantity;
			}
			
			totalPrice += totalProductPrice;
			totalVat += row.vatPerc/100f * totalProductPrice;
			
			productsTable.startNewRow();
			productsTable.addCell(new Cell().add(new Paragraph(row.id)).setBorder(Border.NO_BORDER));
			productsTable.addCell(new Cell().add(new Paragraph(row.description)).setBorder(Border.NO_BORDER).setMaxWidth(UnitValue.createPointValue(70f)));
			productsTable.addCell(new Cell().add(new Paragraph(row.quantity.toString())).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
			productsTable.addCell(new Cell().add(new Paragraph("€" + new BigDecimal((double) row.price).setScale(2, RoundingMode.HALF_UP).toString()).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
			
			if(rowDiscount) {
				productsTable.addCell(new Cell().add(new Paragraph(new BigDecimal((double) row.discountPerc).setScale(1, RoundingMode.HALF_UP).toString() + "%").setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
				productsTable.addCell(new Cell().add(new Paragraph("€" + new BigDecimal((double) totalProductPrice).setScale(2, RoundingMode.HALF_UP).toString()).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
			}
			
			productsTable.addCell(new Cell().add(new Paragraph(new BigDecimal((double) row.vatPerc).setScale(1, RoundingMode.HALF_UP).toString() + "%").setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
			productsTable.addCell(new Cell().add(new Paragraph("€" + new BigDecimal((double) totalProductPrice).setScale(2, RoundingMode.HALF_UP).toString()).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));			
		
			//If the product specified a comment, we want to add that too.
			//The comment cell will have a different collumn span depending on if we have a discount too
			if(row.comment != null) {
				productsTable.startNewRow();
				productsTable.addCell(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
				
				Cell c = new Cell(1, (rowDiscount) ? 7 : 5);
				c.setBorder(Border.NO_BORDER);
				c.add(new Paragraph(row.comment));
				productsTable.addCell(c);
			}
		}
		
		doc.add(productsTable);
		
		//If there is a general note, let's attach it to thre invoice
		if(body.notes != null) {
			doc.add(new Paragraph(body.notes));
		}
		
		//Now let's construct a table containing the totals for the invoice
		//- Total price excluding VAT
		//- Total VAT
		//- Total price including VAT
		//Headers are on the left side here, so they are per row.
		Table totalsTable = new Table(new float[] {4f, 1f, 3f});
		totalsTable.setWidth(pdf.getDefaultPageSize().getWidth() / 3);
		totalsTable.startNewRow();
		totalsTable.addCell(new Cell().add(new Paragraph(languageModel.totalExVat)).setBold().setBorder(Border.NO_BORDER));
		totalsTable.addCell(new Cell().add(new Paragraph("€")).setBorder(Border.NO_BORDER));
		totalsTable.addCell(new Cell().add(new Paragraph(new BigDecimal((double) totalPrice).setScale(2, RoundingMode.HALF_UP).toString())).setBorder(Border.NO_BORDER));
		
		totalsTable.startNewRow();
		totalsTable.addCell(new Cell().add(new Paragraph(languageModel.totalVat)).setBold().setBorder(Border.NO_BORDER));
		totalsTable.addCell(new Cell().add(new Paragraph("€")).setBorder(Border.NO_BORDER));
		totalsTable.addCell(new Cell().add(new Paragraph(new BigDecimal((double) totalVat).setScale(2, RoundingMode.HALF_UP).toString())).setBorder(Border.NO_BORDER));

		totalsTable.startNewRow();
		totalsTable.addCell(new Cell().add(new Paragraph(languageModel.totalPrice)).setBold().setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)));
		totalsTable.addCell(new Cell().add(new Paragraph("€")).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)));
		totalsTable.addCell(new Cell().add(new Paragraph(new BigDecimal((double) (totalPrice + totalVat)).setScale(2, RoundingMode.HALF_UP).toString())).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)));
		
		//Let's create a 'custom' renderer so we can grab the actual width of the totalsTable
		IRenderer tableRenderer = totalsTable.createRendererSubTree().setParent(doc.getRenderer());
		LayoutResult tableLayoutResult = tableRenderer.layout(new LayoutContext(new LayoutArea(0, new Rectangle(pdf.getDefaultPageSize().getWidth(), 1000f))));
		
		//These are mostly just to make the code more readable
		float totalWidth = tableLayoutResult.getOccupiedArea().getBBox().getWidth();
		float leftMargin = doc.getLeftMargin();
		float leftOffset = pdf.getDefaultPageSize().getWidth() - leftMargin - totalWidth;
		
		//Set the fixed position of the table
		//This is at the bottom right of the entire document, accounting for margins
		totalsTable.setFixedPosition(leftOffset, doc.getBottomMargin(), totalWidth);
		doc.add(totalsTable);

		//Lastly, save the document
		doc.close();
	}
}
