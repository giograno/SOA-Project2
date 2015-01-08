import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdf_extractor.PDFExtractor;

public class TFIDF {

	private String input_directory_path;
	private String output_file_path;

	public TFIDF(String input_directory_path, String output_file_path) {
		this.input_directory_path = input_directory_path;
		this.output_file_path = output_file_path;
	}

	public void extractFeatures() throws IOException {
		// da sostituire con scrittura su disco
		List<DocumentVector> vectorList = new ArrayList<DocumentVector>();
		FeaturesExtractor featuresVector = null;

		/* FilenameFilter for PDF to prevent strange errors */
		File[] files = new File(this.input_directory_path)
				.listFiles(new FilenameFilter() {
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
		
		DocumentVector documentVector;
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
	}// fine extractFeatures

}
