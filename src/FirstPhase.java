import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.Dictionary;
import pdf_extractor.PDFExtractor;
import posTagger.POSutils;
import text_analysis.TextPreProcesser;
import text_analysis.Stemmer;
import text_analysis.WordNetUtilities;

public class FirstPhase {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {

		String inputDirectory = "input-files";
		
		POSutils poSutils;
		File[] files = new File(inputDirectory).listFiles();
		String nameDocument;
		int i = 0;
		for (File file : files) {
//			if (i >= 1)
//				break;
			String textExtracted = PDFExtractor
					.extractTextFromPDFDocument(file);
			nameDocument = file.getName();
			i++;

			/* Algoritmo con POS Tagger */
			poSutils = new POSutils();
			String result = poSutils.getLemmatization(nameDocument, textExtracted);

		}
		
		/* fase 2 */
	}

}
