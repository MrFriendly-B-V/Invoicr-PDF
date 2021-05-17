package dev.array21.invoicrpdf.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

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
		
		PdfDocument pdf;
		try {
			pdf = new PdfDocument(new PdfWriter(new File(template.outDir, fileId + ".pdf")));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		Document doc = new Document(pdf);		
		doc.setMargins(40f, 30f, 40f, 30f);		
		
		Paragraph addressing = new Paragraph();
		addressing.add(new Text(body.receiver + "\n").setBold());
		addressing.add(new Text(body.address.street + "\n"));
		addressing.add(new Text(body.address.postalCode + " " + body.address.city + "\n"));
		addressing.add(new Text(body.address.country));
		
		Table heading = new Table(new float[] {1f, 1f});
		heading.startNewRow();
		heading.setHorizontalAlignment(HorizontalAlignment.CENTER);

		Cell addressingCell = new Cell();
		addressingCell.add(addressing);
		addressingCell.setWidth(UnitValue.createPercentValue(50f));
		addressingCell.setBorder(Border.NO_BORDER);
		heading.addCell(addressingCell);
		
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
		
		Paragraph invoiceHeader = new Paragraph(languageModel.invoiceHeader);
		invoiceHeader.setHorizontalAlignment(HorizontalAlignment.LEFT);
		invoiceHeader.setWidth(pdf.getDefaultPageSize().getWidth() - doc.getLeftMargin() - doc.getRightMargin());
		invoiceHeader.setTextAlignment(TextAlignment.RIGHT);
		invoiceHeader.setBold();
		invoiceHeader.setFontSize(30);		
		doc.add(invoiceHeader);

		doc.add(new LineSeparator(blackLine));
		Table referenceTable = new Table(new float[] {3, 1, 1, 1});
		referenceTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		referenceTable.useAllAvailableWidth();
		referenceTable.startNewRow();
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.reference).setBold()).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.invoiceDate).setBold()).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.expiryDate).setBold()).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(languageModel.invoiceId).setBold()).setBorder(Border.NO_BORDER));
		
		referenceTable.startNewRow();
		referenceTable.addCell(new Cell().add(new Paragraph(body.reference)).setBorder(Border.NO_BORDER));
		
		Date expiryDate = new Date(body.expDate);
		referenceTable.addCell(new Cell().add(new Paragraph(dateFormatter.format(expiryDate))).setBorder(Border.NO_BORDER));
		
		Date invoiceDate = new Date(body.invoiceDate);
		referenceTable.addCell(new Cell().add(new Paragraph(dateFormatter.format(invoiceDate))).setBorder(Border.NO_BORDER));
		referenceTable.addCell(new Cell().add(new Paragraph(body.invoiceId)).setBorder(Border.NO_BORDER));
		doc.add(referenceTable);

		doc.add(new LineSeparator(blackLine));
		
		boolean rowDiscount = body.hasRowDiscount();
		float[] requestRowLayout;
		if(rowDiscount) {
			requestRowLayout = new float[] { 1f, 2f, 1f, 1f, 1f, 1f, 1f, 1f };
		} else {
			requestRowLayout = new float[] { 1f, 2f, 1f, 1f, 1f, 1f};
		}
		
		Table productsTable = new Table(requestRowLayout);
		productsTable.startNewRow();
		productsTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		productsTable.useAllAvailableWidth();
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productId).setBold()).setBorder(Border.NO_BORDER));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productDescription).setBold()).setBorder(Border.NO_BORDER));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productQuantity).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productPrice).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		if(rowDiscount) {
			productsTable.addCell(new Cell().add(new Paragraph(languageModel.productDiscount).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
			productsTable.addCell(new Cell().add(new Paragraph(languageModel.productAmount).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		}
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productVat).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		productsTable.addCell(new Cell().add(new Paragraph(languageModel.productTotal).setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		
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
		}
		
		doc.add(productsTable);

		doc.close();
	}
}
