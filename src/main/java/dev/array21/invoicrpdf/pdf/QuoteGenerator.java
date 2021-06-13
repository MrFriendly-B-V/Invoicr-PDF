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

import dev.array21.invoicrpdf.InvoicrPdf;
import dev.array21.invoicrpdf.gson.config.ModelType;
import dev.array21.invoicrpdf.gson.config.QuoteLanguageModel;
import dev.array21.invoicrpdf.gson.config.Template;
import dev.array21.invoicrpdf.gson.partials.ItemRow;
import dev.array21.invoicrpdf.gson.requests.PdfQuoteRequest;
import dev.array21.invoicrpdf.util.PdfUtil;

public strictfp class QuoteGenerator {
	
	private final PdfQuoteRequest body;
	private final static String EURO = "\u20ac";
	
	public QuoteGenerator(PdfQuoteRequest body) {
		this.body = body;
	}
	
	public strictfp void generate(String fileId) {
		Template template = InvoicrPdf.getConfig().getTemplate(body.templateName);
		QuoteLanguageModel languageModel = (QuoteLanguageModel) template.getLanguageModel(body.language, ModelType.QUOTE);
		if(languageModel == null) {
			InvoicrPdf.logErr("Unable to find quote language model for language " + body.language);
			return;
		}
		
		//Simply, a black line
		SolidLine blackLine = new SolidLine(1f);
		blackLine.setColor(ColorConstants.BLACK);
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-mm-YYYY");
		
		//Lets create the document
		PdfDocument pdf;
		try {
			pdf = new PdfDocument(new PdfWriter(new File(InvoicrPdf.getConfig().outDir, fileId + ".quote.pdf")));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		Document doc = new Document(pdf);		
		doc.setMargins(40f, 30f, 40f, 30f);
		
		//The addressing is at the top of the quote, it describes for whom it's ment
		Paragraph addressing = new Paragraph();
		addressing.add(new Text(body.receiver + "\n").setBold());
		if(body.attentionOf != null) {
			addressing.add(new Text(body.attentionOf + "\n"));
		}
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
		
		//The header is simple, it screams QUOTE (depending on the languageModel used)
		Paragraph header = new Paragraph(languageModel.quoteHeader);
		header.setHorizontalAlignment(HorizontalAlignment.LEFT);
		header.setWidth(pdf.getDefaultPageSize().getWidth() - doc.getLeftMargin() - doc.getRightMargin());
		header.setTextAlignment(TextAlignment.RIGHT);
		header.setBold();
		header.setFontSize(30);		
		doc.add(header);
		
		Table referenceTable = new Table(new float[] {1f, 1f});
		referenceTable.setBorder(Border.NO_BORDER);
		referenceTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		referenceTable.useAllAvailableWidth();
		referenceTable.startNewRow();
		
		Table leftReference = new Table(new float[] { 1f, 1f});
		leftReference.startNewRow();
		leftReference.addCell(PdfUtil.getCell(languageModel.quoteId, true, null));
		leftReference.addCell(PdfUtil.getCell(": " + body.id.toString()));
		
		leftReference.startNewRow();
		leftReference.addCell(PdfUtil.getCell(languageModel.quoteDate, true, null));
		Date creationDate = new Date(body.creationDate);
		leftReference.addCell(PdfUtil.getCell(": " + dateFormatter.format(creationDate)));

		leftReference.startNewRow();
		leftReference.addCell(PdfUtil.getCell(languageModel.quoteExpiryDate, true, null));
		Date expiryDate = new Date(body.expiryDate);
		leftReference.addCell(PdfUtil.getCell(": " + dateFormatter.format(expiryDate)));
		
		leftReference.startNewRow();
		leftReference.addCell(PdfUtil.getCell(languageModel.contactPerson, true, null));
		leftReference.addCell(PdfUtil.getCell(": " + body.quoteContactPerson));
		
		referenceTable.addCell(new Cell().add(leftReference).setBorder(Border.NO_BORDER));
		
		Table rightReference = new Table(new float[] {1f, 1f});
		rightReference.startNewRow();
		rightReference.addCell(PdfUtil.getCell(languageModel.debitId, true, null));
		rightReference.addCell(PdfUtil.getCell(": " + body.debitId));
		
		rightReference.startNewRow();
		rightReference.addCell(PdfUtil.getCell(languageModel.reference, true, null));
		rightReference.addCell(PdfUtil.getCell(": " + ((body.reference != null) ? body.reference : "")));
		
		referenceTable.addCell(new Cell().add(rightReference).setBorder(Border.NO_BORDER));
		doc.add(referenceTable);
		
		Table topicTable = new Table(new float[] {1f, 1f});
		topicTable.startNewRow();
		topicTable.addCell(PdfUtil.getCell(languageModel.topic, true, null));
		topicTable.addCell(PdfUtil.getCell(": " + ((body.quoteTopic != null) ? body.quoteTopic : "")));
		
		doc.add(topicTable);
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
		productsTable.addCell(PdfUtil.getCell(languageModel.productId, true, null));
		productsTable.addCell(PdfUtil.getCell(languageModel.productDescription, true, null));
		productsTable.addCell(PdfUtil.getCell(languageModel.productQuantity, true, TextAlignment.CENTER));
		productsTable.addCell(PdfUtil.getCell(languageModel.productPrice, true, TextAlignment.RIGHT));
		
		//If a discount is given, add those headers too
		if(rowDiscount) {
			productsTable.addCell(PdfUtil.getCell(languageModel.productDiscount, true, TextAlignment.RIGHT));
			productsTable.addCell(PdfUtil.getCell(languageModel.productAmount, true, TextAlignment.RIGHT));
		}
		
		productsTable.addCell(PdfUtil.getCell(languageModel.productVat, true, TextAlignment.RIGHT));
		productsTable.addCell(PdfUtil.getCell(languageModel.productTotal, true, TextAlignment.RIGHT));
		
		//Now let's iterate over every product and add it to the table
		//We'll also calculate the totalPrice and totalVat while we're at it
		float totalPrice = 0f;
		float totalVat = 0f;
		for(ItemRow row : body.rows) {
			float totalProductPrice = 0f;
			if(rowDiscount) {
				totalProductPrice = row.price * (100f - row.discountPerc)/100f * (float) row.quantity;
			} else {
				totalProductPrice = row.price * (float) row.quantity;
			}
			
			totalPrice += totalProductPrice;
			totalVat += row.vatPerc/100f * totalProductPrice;
			
			productsTable.startNewRow();
			productsTable.addCell(PdfUtil.getCell(row.id));
			productsTable.addCell(PdfUtil.getCell(row.description).setMaxWidth(UnitValue.createPointValue(70f)));
			productsTable.addCell(PdfUtil.getCell(row.quantity.toString(), false, TextAlignment.CENTER));
			productsTable.addCell(PdfUtil.getCell(EURO + new BigDecimal((double) row.price).setScale(2, RoundingMode.HALF_UP).toString(), false, TextAlignment.RIGHT));
			
			if(rowDiscount) {
				productsTable.addCell(PdfUtil.getCell(new BigDecimal((double) row.discountPerc).setScale(1, RoundingMode.HALF_UP).toString() + "%", false, TextAlignment.CENTER));
				productsTable.addCell(PdfUtil.getCell(EURO + new BigDecimal((double) totalProductPrice / row.quantity).setScale(2, RoundingMode.HALF_UP).toString(), false, TextAlignment.RIGHT));
			}
			
			productsTable.addCell(PdfUtil.getCell(new BigDecimal((double) row.vatPerc).setScale(1, RoundingMode.HALF_UP).toString() + "%", false, TextAlignment.CENTER));
			productsTable.addCell(PdfUtil.getCell(EURO + new BigDecimal((double) totalProductPrice).setScale(2, RoundingMode.HALF_UP).toString(), false, TextAlignment.RIGHT));				
		
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
		totalsTable.addCell(PdfUtil.getCell(languageModel.totalExclVat, true, null));
		totalsTable.addCell(PdfUtil.getCell(EURO));
		totalsTable.addCell(PdfUtil.getCell(new BigDecimal((double) totalPrice).setScale(2, RoundingMode.HALF_UP).toString()));
		
		totalsTable.startNewRow();
		totalsTable.addCell(PdfUtil.getCell(languageModel.totalVat, true, null));
		totalsTable.addCell(PdfUtil.getCell(EURO));
		totalsTable.addCell(PdfUtil.getCell(new BigDecimal((double) totalVat).setScale(2, RoundingMode.HALF_UP).toString()));

		totalsTable.startNewRow();
		totalsTable.addCell(PdfUtil.getCell(languageModel.totalInclVat)).setBorderTop(new SolidBorder(1f));
		totalsTable.addCell(PdfUtil.getCell(EURO).setBorderTop(new SolidBorder(1f)));
		totalsTable.addCell(PdfUtil.getCell(new BigDecimal((double) (totalPrice + totalVat)).setScale(2, RoundingMode.HALF_UP).toString())).setBorderTop(new SolidBorder(1f));
	
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
