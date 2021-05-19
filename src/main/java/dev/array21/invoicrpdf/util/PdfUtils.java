package dev.array21.invoicrpdf.util;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

public class PdfUtils {

	public static Cell getCell(String text, boolean bold, TextAlignment alignment) {
		Cell cell = new Cell();
		//cell.setBorder(Border.NO_BORDER);
		cell.add(new Paragraph(text));
		//.setTextAlignment(alignment);
		if(bold) {
			cell.setBold();
		}
		
		return cell;
	}
	
	public static Cell getCell(String text) {
		return PdfUtils.getCell(text, false, TextAlignment.LEFT);
	}
	
	public static Cell getRightAlignedCell(String text) {
		return PdfUtils.getCell(text, false, TextAlignment.RIGHT);
	}
	
	public static Cell getRightAlignedCell(String text, boolean bold) {
		return PdfUtils.getCell(text, bold, TextAlignment.RIGHT);
	}
}
