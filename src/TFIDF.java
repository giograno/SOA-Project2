import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFIDF implements DocumentFeatures {

	private String input_directory_path;
	private String output_file_path;
	private final String INTERMEDIATE_DIR_PATH = "intermediate_document_vectors";

	public TFIDF(String input_directory_path, String output_file_path) {
		this.input_directory_path = input_directory_path;
		this.output_file_path = output_file_path;
	}

	public void extractFeatures() {
		// da sostituire con scrittura su disco
		List<DocumentVector> vectorList = new ArrayList<DocumentVector>();
		FeaturesExtractor featuresVector = null;

		/* FilenameFilter for PDF to prevent strange errors */
		File[] input_files = new File(this.input_directory_path)
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

		/* FASE 1: scrittura classi serializzabili */
		DocumentVector documentVector = null;
		for (File file : input_files) {
			nameDocument = file.getName().substring(0,
					file.getName().length() - 4);
			System.out.println(nameDocument);
			featuresVector = new FeaturesExtractor(nameDocument);
			try {
				documentVector = featuresVector.getDocumentVector(PDFExtractor
						.extractTextFromPDFDocument(file));
			} catch (IOException e) {
				e.printStackTrace();
			}

			/* per farlo ancora funzionare */
			vectorList.add(documentVector);
			/* fine */

			try {
				FileOutputStream fileOutputStream = new FileOutputStream(
						INTERMEDIATE_DIR_PATH + "/" + nameDocument + ".ser");
				ObjectOutputStream outputStream = new ObjectOutputStream(
						fileOutputStream);
				outputStream.writeObject(documentVector);
				outputStream.close();
				fileOutputStream.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			documentInCorpus++;
		}
		Map<String, Double> termFrequencyInverseDocumentFrequency;
		Map<String, Integer> termFrequency;
		int numberOfWordInDocument;

		/* FilenameFilter for .ser to prevent strange errors */
		File[] temporary_files = new File(INTERMEDIATE_DIR_PATH)
				.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						if (name.endsWith(".ser"))
							return true;
						else
							return false;
					}
				});

		DocumentVector documentVector2 = null;
		int i = 1;
		for (File file : temporary_files) {

			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				ObjectInputStream inputStream = new ObjectInputStream(
						fileInputStream);
				documentVector2 = (DocumentVector) inputStream.readObject();
				inputStream.close();
				fileInputStream.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("DocumentVector class not found!");
				e.printStackTrace();
			}

			termFrequencyInverseDocumentFrequency = new HashMap<>();
			termFrequency = documentVector2.getTermFrequency();
			numberOfWordInDocument = documentVector2
					.getNumberOfWordInDocument();

			for (String string : termFrequency.keySet()) {
				double tf = (double) termFrequency.get(string)
						/ (double) numberOfWordInDocument;
				double idf = (double) documentInCorpus
						/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
								.get(string);
				double tfidf = tf * Math.log10(idf);
				termFrequencyInverseDocumentFrequency.put(string, tfidf);
			}

			System.out.println("Stampa matrice numero: " + (i + 1));
			for (String string : termFrequencyInverseDocumentFrequency.keySet()) {
				System.out.println("key: " + string + ", value: "
						+ termFrequencyInverseDocumentFrequency.get(string));
			}
		}
	}

}
