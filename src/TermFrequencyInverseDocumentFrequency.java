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

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import parsing.PDFFile;
import parsing.SimonePDFParser;
import utils.Utils;

public class TermFrequencyInverseDocumentFrequency implements Feature {

	private String input_directory_path;
	private String output_file_path;
	private final String INTERMEDIATE_DIR_PATH = "intermediate_document_vectors";
	private MaxentTagger tagger = null;

	public TermFrequencyInverseDocumentFrequency(String input_directory_path,
			String output_file_path) {
		this.input_directory_path = input_directory_path;
		this.output_file_path = output_file_path + "/output.txt";
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
		SimonePDFParser simoneParser = new SimonePDFParser();

		for (File file : input_files) {
			nameDocument = file.getName().substring(0,
					file.getName().length() - 4);
			System.out.println("Extraction from: " + nameDocument);
			extractor = new FeaturesExtractor(nameDocument, tagger);

			/* APACHE TIKA */

			try {
				PDFFile pFile = simoneParser.parse(file);
				documentVector = extractor.getDocumentVectorV2(PDFExtractor
						.processingText(pFile.getContent()));
			} catch (IOException | SAXException | TikaException e1) {
				e1.printStackTrace();
			}

			/* PDFBOX */

			// try {
			// documentVector = extractor.getDocumentVector(PDFExtractor
			// .extractTextFromPDFDocument(file));
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			writeSerializedObjectOnFile(documentVector, nameDocument);
			documentInCorpus++;
		}

		Map<String, Double> termFrequencyInverseDocumentFrequency;
		Map<String, Integer> termFrequency;
		int numberOfWordInDocument;

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
				writeFeaturesOnFile(documentVector,
						termFrequencyInverseDocumentFrequency, documentIndex);
				documentIndex++;
			} catch (IOException e) {
				System.err.println("You have a problem on saving files!");
				e.printStackTrace();
			}
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
			int documentIndex) throws IOException {
		File outputFile = new File(this.output_file_path);

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

}
