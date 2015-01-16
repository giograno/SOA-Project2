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

import utils.Utils;

public class TFIDF implements DocumentFeatures {

	private String input_directory_path;
	private String output_file_path;
	private final String INTERMEDIATE_DIR_PATH = "intermediate_document_vectors";

	public TFIDF(String input_directory_path, String output_file_path) {
		this.input_directory_path = input_directory_path;
		this.output_file_path = output_file_path + "/output.txt";
		Utils.cleanOrCreateDirectory(this.INTERMEDIATE_DIR_PATH);
	}

	public void extractFeatures() {
		FeaturesExtractor featuresVector = null;

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
			featuresVector = new FeaturesExtractor(nameDocument);
			try {
				documentVector = featuresVector.getDocumentVector(PDFExtractor
						.extractTextFromPDFDocument(file));
			} catch (IOException e) {
				e.printStackTrace();
			}

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

		File[] temporary_files = Utils.listSerializedInDirectory(INTERMEDIATE_DIR_PATH);
		
		int i = 1;
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

			termFrequencyInverseDocumentFrequency = new HashMap<>();
			termFrequency = documentVector.getTermFrequency();
			numberOfWordInDocument = documentVector.getNumberOfWordInDocument();

			for (String string : termFrequency.keySet()) {
				System.out.println(string + ": " + termFrequency.get(string));
				double tf = (double) termFrequency.get(string)
						/ (double) numberOfWordInDocument;
				double idf = (double) documentInCorpus
						/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
								.get(string);
				double tfidf = tf * Math.log10(idf);
				termFrequencyInverseDocumentFrequency.put(string, tfidf);
			}

			try {
				File outputFile = new File(this.output_file_path);
				if (!file.exists()) {
					outputFile.createNewFile();
				}
				FileWriter fileWriter = new FileWriter(outputFile, true);

				String recordToWrite = "doc#" + i + "@"
						+ documentVector.getName() + ";";
				for (String record : termFrequencyInverseDocumentFrequency
						.keySet()) {
					recordToWrite += record + ":"
							+ termFrequencyInverseDocumentFrequency.get(record)
							+ ";";
				}
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(recordToWrite);
				bufferedWriter.write("\n");
				bufferedWriter.close();
				i++;
			} catch (IOException e) {
				System.err.println("You have a problem on saving files!");
				e.printStackTrace();
			}
		}
	}
}
