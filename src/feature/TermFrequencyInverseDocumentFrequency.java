package feature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import utils.Constants;
import utils.Utils;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import extractor.PDFExtractor;

public class TermFrequencyInverseDocumentFrequency implements Feature {

	private String input_directory_path;
	private String output_tfidf_path;
	private String output_tf_path;
	private final String INTERMEDIATE_DIR_PATH = "intermediate_document_vectors";
	private MaxentTagger tagger = null;

	public TermFrequencyInverseDocumentFrequency(String input_directory_path,
			String output_file_path) {
		this.input_directory_path = input_directory_path;
		this.output_tfidf_path = output_file_path + "/TFIDF.txt";
		this.output_tf_path = output_file_path + "/TF.txt";
		Utils.cleanOrCreateDirectory(this.INTERMEDIATE_DIR_PATH);
		try {
			tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	public void extractFeatures() {
		FeaturesExtractor extractor = null;

		File[] input_files = Utils.listPDFinDirectory(input_directory_path);

		// Name of current document in analysis
		String nameDocument;
		// Total number of document to analyze
		int documentInCorpus = 0;

		/* FASE 1: scrittura classi serializzabili */
		DocumentVector documentVector = null;

		for (File file : input_files) {
			nameDocument = file.getName().substring(0,
					file.getName().length() - 4);
			System.out.println("Extraction from: " + nameDocument);
			extractor = new FeaturesExtractor(nameDocument, tagger);

			try {
				documentVector = extractor.getDocumentVector(PDFExtractor
						.extractTextFromPDFDocument(file));
			} catch (IOException e) {
				e.printStackTrace();
			}

			writeSerializedObjectOnFile(documentVector, nameDocument);
			documentInCorpus++;
		}

		File[] temporary_files = Utils
				.listSerializedInDirectory(INTERMEDIATE_DIR_PATH);

		int documentIndex = 1;
		for (File file : temporary_files) {

			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				ObjectInputStream inputStream = new ObjectInputStream(
						fileInputStream);
				documentVector = (DocumentVector) inputStream.readObject();
				inputStream.close();
				fileInputStream.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("DocumentVector class not found!");
				e.printStackTrace();
			}

			calculateTFIDF(documentVector, documentInCorpus, documentIndex);
			documentIndex = 1;
			calculateTF(documentVector, documentInCorpus, documentIndex);
		}
	}

	private void calculateTFIDF(DocumentVector documentVector,
			int documentInCorpus, int documentIndex) {

		Map<String, Double> termFrequencyInverseDocumentFrequency = new HashMap<>();

		Map<String, Integer> termFrequency = documentVector.getTermFrequency();
		Map<String, Integer> conceptFrequency = documentVector
				.getConceptFrequency();
		int numberOfWordInDocument = documentVector.getTotalNumberOfWord();

		int lowerLimit = (int) Math.round(Constants.LOWER_LIMIT
				* documentInCorpus / 100);
		int upperLimit = (int) Math.round(Constants.UPPER_LIMIT
				* documentInCorpus / 100);

		/* FOR WORDS */
		for (String string : termFrequency.keySet()) {
			if (NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
					.get(string) > lowerLimit
					&& NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
							.get(string) < upperLimit) {

				double tf = (double) termFrequency.get(string)
						/ (double) numberOfWordInDocument;
				double idf = (double) documentInCorpus
						/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
								.get(string);
				double tfidf = tf * Math.log10(idf);
				termFrequencyInverseDocumentFrequency.put(string, tfidf);
			} else {
				numberOfWordInDocument--;
			}
		}

		/* FOR CONCEPTS */
		for (String string : conceptFrequency.keySet()) {
			if (NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereConceptAppears
					.get(string) > lowerLimit
					&& NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereConceptAppears
							.get(string) < upperLimit) {

				double tf = (double) conceptFrequency.get(string)
						/ (double) numberOfWordInDocument;
				double idf = (double) documentInCorpus
						/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereConceptAppears
								.get(string);
				double tfidf = tf * Math.log10(idf);
				termFrequencyInverseDocumentFrequency.put(string, tfidf);
			} else {
				numberOfWordInDocument--;
			}
		}

		try {
			writeFeaturesOnFile(documentVector,
					termFrequencyInverseDocumentFrequency, documentIndex,
					this.output_tfidf_path);
			documentIndex++;
		} catch (IOException e) {
			System.err.println("You have a problem on saving files!");
			e.printStackTrace();
		}

		try {
			writeAllTermsOnFile(
					NumberOfDocumentsWhereWordAppears
							.getNumberOfDocumentsWhereWordAppears(),
					NumberOfDocumentsWhereWordAppears
							.getNumberOfDocumentsWhereConceptAppears());
		} catch (IOException e) {
			System.err.println("Some problems on saving all words on file!");
			e.printStackTrace();
		}
	}

	private void calculateTF(DocumentVector documentVector,
			int documentInCorpus, int documentIndex) {

		Map<String, Double> termFrequencyFeature = new HashMap<>();

		Map<String, Integer> termFrequency = documentVector.getTermFrequency();

		int numberOfWordInDocument = documentVector.getNumberOfWordInDocument();

		int lowerLimit = (int) Math.round(Constants.LOWER_LIMIT
				* documentInCorpus / 100);
		int upperLimit = (int) Math.round(Constants.UPPER_LIMIT
				* documentInCorpus / 100);

		/* FOR WORDS */
		for (String string : termFrequency.keySet()) {
			if (NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
					.get(string) > lowerLimit
					&& NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
							.get(string) < upperLimit) {

				double tf = (double) termFrequency.get(string)
						/ (double) numberOfWordInDocument;
				termFrequencyFeature.put(string, tf);
			} else {
				numberOfWordInDocument--;
			}
		}

		try {
			writeFeaturesOnFile(documentVector, termFrequencyFeature,
					documentIndex, this.output_tf_path);
			documentIndex++;
		} catch (IOException e) {
			System.err.println("You have a problem on saving files!");
			e.printStackTrace();
		}
	}

	private void writeSerializedObjectOnFile(DocumentVector pDocumentVector,
			String pNameDocument) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					INTERMEDIATE_DIR_PATH + "/" + pNameDocument + ".ser");
			ObjectOutputStream outputStream = new ObjectOutputStream(
					fileOutputStream);
			outputStream.writeObject(pDocumentVector);
			outputStream.close();
			fileOutputStream.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void writeFeaturesOnFile(DocumentVector pDocumentVector,
			Map<String, Double> pTermFrequencyInverseDocumentFrequency,
			int documentIndex, String outputPath) throws IOException {
		File outputFile = new File(outputPath);

		FileWriter fileWriter = new FileWriter(outputFile, true);

		String recordToWrite = "doc#" + documentIndex + "@"
				+ pDocumentVector.getName() + ";";
		for (String record : pTermFrequencyInverseDocumentFrequency.keySet()) {
			recordToWrite += record + ":"
					+ pTermFrequencyInverseDocumentFrequency.get(record) + ";";
		}
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(recordToWrite);
		bufferedWriter.write("\n");
		bufferedWriter.close();
	}

	private void writeAllTermsOnFile(Map<String, Integer> frequencyMap,
			Map<String, Integer> conceptMap) throws IOException {
		File outputFile = new File("output/allWords.txt");
		FileWriter fileWriter = new FileWriter(outputFile, true);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (String word : frequencyMap.keySet()) {
			bufferedWriter.write(word + "\n");
		}
		for (String word : conceptMap.keySet()) {
			bufferedWriter.write(word + "\n");
		}
		bufferedWriter.close();
	}

}
