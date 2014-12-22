package pdf_extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFExtractor {
	private static final String STOP_WORDS = "stop-word";

	/**
	 * Extract text from a PDF File
	 * 
	 * @param pDocument
	 *            PDF File
	 * @return a String which contains text extracted
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static String extractTextFromPDFDocument(File pDocument) {
		PDFTextStripper pdfTextStripper = null;
		PDDocument pdDocument = null;
		COSDocument cosDocument = null;
		String extractText = null;

		try {
			PDFParser parser = new PDFParser(new FileInputStream(pDocument));
			parser.parse();
			cosDocument = parser.getDocument();
			pdfTextStripper = new PDFTextStripper();
			pdDocument = new PDDocument(cosDocument);
			extractText = pdfTextStripper.getText(pdDocument);
			pdDocument.close();
			cosDocument.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return extractText;
	}

	public static String processingText(String document) throws IOException {
		StringBuilder sBuilder = new StringBuilder();
		try {
			List<String> stopWords = FileUtils.readLines(new File(STOP_WORDS));
			CharArraySet stopWSet = new CharArraySet(stopWords, true);

			TokenStream tokenStream = new LetterTokenizer(new StringReader(
					document.trim()));
			tokenStream = new StopFilter(tokenStream, stopWSet);

			CharTermAttribute charTermAttribute = tokenStream
					.addAttribute(CharTermAttribute.class);

			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String string = charTermAttribute.toString();
				sBuilder.append(string + " ");
			}
			tokenStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sBuilder.toString();
	}
}
