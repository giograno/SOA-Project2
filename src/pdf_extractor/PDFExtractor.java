package pdf_extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFExtractor {
	private PDFTextStripper pdfTextStripper = null;
	private PDDocument pdDocument = null;
	private COSDocument cosDocument = null;
	private String extractText = null;

	/**
	 * Extract text from a PDF File
	 * 
	 * @param pDocument
	 *            PDF File
	 * @return a String which contains text extracted
	 */
	public String extractTextFromPDFDocument(File pDocument) {
		try {
			PDFParser parser = new PDFParser(new FileInputStream(pDocument));
			parser.parse();
			cosDocument = parser.getDocument();
			pdfTextStripper = new PDFTextStripper();
			pdDocument = new PDDocument(cosDocument);
			this.extractText = pdfTextStripper.getText(pdDocument);
			pdDocument.close();
			cosDocument.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return extractText;
	}
}
