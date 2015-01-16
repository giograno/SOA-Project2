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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFExtractor {
	private static final String STOP_WORDS = "stop-word";

	/**
	 * Extract text from PDF file
	 * 
	 * @param pDocument
	 *            PDF file
	 * @return a String which contains text
	 * @throws IOException
	 */
	public static String extractTextFromPDFDocument(File pDocument)
			throws IOException {
		PDFTextStripper pdfTextStripper = null;
		PDDocument pdDocument = null;
		String extractText = null;

		try {
			PDFParser parser = new PDFParser(new FileInputStream(pDocument));
			parser.parse();
			pdDocument = parser.getPDDocument();
			pdfTextStripper = new PDFTextStripper();
			extractText = pdfTextStripper.getText(pdDocument);
			pdDocument.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return processingText(extractText);
	}

	/**
	 * Perform stop word removing and tokenization on text extracted
	 * 
	 * @param document
	 * @return
	 */
	private static String processingText(String document) {
		StringBuilder sBuilder = new StringBuilder();
		try {
			List<String> stopWords = FileUtils.readLines(new File(STOP_WORDS));
			CharArraySet stopWSet = new CharArraySet(stopWords, true);

			TokenStream tokenStream = new LetterTokenizer(new StringReader(
					document.trim()));
			tokenStream = new StopFilter(tokenStream, stopWSet);
//			tokenStream = new LowerCaseFilter(tokenStream);

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
