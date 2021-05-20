package dev.array21.invoicrpdf.util;

import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

public class PdfUtil {

	public static Cell getCell(String text, boolean bold, TextAlignment alignment) {
		Cell cell = new Cell();
		cell.setBorder(Border.NO_BORDER);
		cell.add(new Paragraph(text));
		
		if(alignment != null) {
			cell.setTextAlignment(alignment);
		}
		
		if(bold) {
			cell.setBold();
		}
		
		return cell;
	}
	
	public static Cell getCell(String text) {
		return PdfUtil.getCell(text, false, null);
	}
}
