import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.clapper.util.misc.FileHashMap;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

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
import text_analysis.Stemmer;
import text_analysis.WordNetUtilities;

public class SequentialTFIDF {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException, ObjectExistsException, VersionMismatchException {

		/*
		 * input-files: for real data tfidf: small dummy test data
		 */
		final String INPUT_DIR = "input-files";
		final String FEATURE_OUTPUT = "features-vectors";

		// da sostituire con scrittura su disco
		List<DocumentVector> vectorList = new ArrayList<DocumentVector>();
		FeaturesExtractor featuresVector = null;

		/* FilenameFilter for PDF to prevent strange errors */
		File[] files = new File(INPUT_DIR).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".pdf"))
					return true;
				else
					return false;
			}
		});

		// Name of current document in analysis
		String nameDocument;
		// Total number of document to analyze
		int documentInCorpus = 0;

		for (File file : files) {
			nameDocument = file.getName();
			featuresVector = new FeaturesExtractor(nameDocument);
			vectorList.add(featuresVector.getDocumentVector(PDFExtractor
					.extractTextFromPDFDocument(file)));
			documentInCorpus++;
		}

		Map<String, Double> termFrequencyInverseDocumentFrequency;
		Map<String, Integer> termFrequency;
		int numberOfWordInDocument;
		for (int i = 0; i < documentInCorpus; i++) {
			termFrequencyInverseDocumentFrequency = new HashMap<>();
			termFrequency = vectorList.get(i).getTermFrequency();
			numberOfWordInDocument = vectorList.get(i)
					.getNumberOfWordInDocument();

			/* ciclo su tutte le parole della term frequency map */
			for (String string : termFrequency.keySet()) {

				// tf: n/N
				double tf = (double) termFrequency.get(string)
						/ (double) numberOfWordInDocument;
				// idf: d/D
				double idf = (double) documentInCorpus
						/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
								.get(string);
				// tfidf: tf * log(idf)
				double tfidf = tf * Math.log10(idf);
				termFrequencyInverseDocumentFrequency.put(string, tfidf);
			}

			System.out.println("Stampa matrice numero: " + (i + 1));
			for (String string : termFrequencyInverseDocumentFrequency.keySet()) {
				System.out.println("key: " + string + ", value: "
						+ termFrequencyInverseDocumentFrequency.get(string));
			}
		}
	}//end on main
}
