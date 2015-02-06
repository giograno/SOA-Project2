package feature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import utils.Constants;
import utils.Utils;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import extractor.PDFExtractor;

public class TermFrequencyAndInverse implements Feature {

	private String input_directory_path;
	private String output_tfidf_path;
	private String output_tf_path;
	private final String INTERMEDIATE_DIR_PATH = "intermediate_document_vectors";
	private MaxentTagger tagger = null;

	public TermFrequencyAndInverse(String input_directory_path,
			String output_file_path) {
		this.input_directory_path = input_directory_path;
		this.output_tfidf_path = output_file_path + "/TFIDF.txt";
		this.output_tf_path = output_file_path + "/TF.txt";
		Utils.cleanOrCreateDirectory(this.INTERMEDIATE_DIR_PATH);

		tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
	}

	public void extractFeatures() {
		FeaturesExtractor extractor = null;

		// File[] input_files = Utils.listPDFinDirectory(input_directory_path);

		IOFileFilter filter = new SuffixFileFilter(new String("pdf"),
				IOCase.INSENSITIVE);
		Iterator<File> allFiles = FileUtils.iterateFiles(new File(
				input_directory_path), filter, DirectoryFileFilter.DIRECTORY);

		// Name of current document in analysis
		String nameDocument;
		// Total number of document to analyze
		int documentInCorpus = 0;

		/* FASE 1: scrittura classi serializzabili */
		DocumentVector documentVector = null;

		while (allFiles.hasNext()) {
			File file = (File) allFiles.next();
//			String parentDirectory = file.getParentFile().getName();
			// for (File file : input_files) {
//			nameDocument = file.getName().substring(0,
//					file.getName().length() - 4);
			nameDocument = file.getParentFile().getName() + "-"
					+ file.getName().substring(0, file.getName().length() - 4);
			System.out.println("Extraction from: " + nameDocument);
			extractor = new FeaturesExtractor(nameDocument, tagger);

			try {
				String textExtracted = PDFExtractor
						.extractTextFromPDFDocument(file);
				// check some problem on file extraction
				if (textExtracted.isEmpty()) {
					System.err.println("Document with empty text!");
					continue;
				}
				documentVector = extractor.getDocumentVector(textExtracted);
				writeSerializedObjectOnFile(documentVector, nameDocument);
				documentInCorpus++;
			} catch (Exception e) {
				e.printStackTrace();
//				System.err.println("An ecception occurred on PDF extraction");
				continue;
			}
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
			calculateTF(documentVector, documentInCorpus, documentIndex);
			documentIndex++;
		}

		try {
			writeAllTermsOnFile(NumberOfDocumentsWhereWordAppears
					.getNumberOfDocumentsWhereWordAppears());
		} catch (IOException e) {
			System.err.println("Some problems on saving all words on file!");
			e.printStackTrace();
		}

		System.out.println("Document successfully processed = "
				+ documentInCorpus);
		writeNumberOfDocumentOnFile(documentInCorpus);
	}

	private void calculateTFIDF(DocumentVector documentVector,
			int documentInCorpus, int documentIndex) {

		// final results with feature values
		Map<String, Double> termFrequencyInverseDocumentFrequency = new HashMap<>();

		Map<String, Integer> termFrequency = documentVector.getTermFrequency();

		int numberOfWordInDocument = documentVector.getNumberOfWordInDocument();

		/* FOR WORDS */
		for (String string : termFrequency.keySet()) {
			double tf = (double) termFrequency.get(string)
					/ (double) numberOfWordInDocument;
			double idf = (double) documentInCorpus
					/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
							.get(string);
			double tfidf = tf * Math.log10(idf);
			termFrequencyInverseDocumentFrequency.put(string, tfidf);
		}

		/* FOR CONCEPTS */
		if (Constants.CONCEPTS) {
			Map<String, Integer> conceptFrequency = documentVector
					.getConceptFrequency();
			int numberOfConceptInDocument = documentVector
					.getNumberOfSynsetInDocument();

			for (String string : conceptFrequency.keySet()) {
				double tf = (double) conceptFrequency.get(string)
						/ (double) numberOfConceptInDocument;
				double idf = (double) documentInCorpus
						/ (double) NumberOfDocumentsWhereWordAppears.numberOfDocumentsWhereWordAppears
								.get(string);
				double tfidf = tf * Math.log10(idf);
				termFrequencyInverseDocumentFrequency.put(string, tfidf);
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

	}

	private void calculateTF(DocumentVector documentVector,
			int documentInCorpus, int documentIndex) {

		Map<String, Double> termFrequencyFeature = new HashMap<>();

		Map<String, Integer> termFrequency = documentVector.getTermFrequency();

		int numberOfWordInDocument = documentVector.getNumberOfWordInDocument();

		/* FOR WORDS */
		for (String string : termFrequency.keySet()) {
			double tf = (double) termFrequency.get(string)
					/ (double) numberOfWordInDocument;
			termFrequencyFeature.put(string, tf);
		}

		/* FOR CONCEPTS */
		if (Constants.CONCEPTS) {
			Map<String, Integer> conceptFrequency = documentVector
					.getConceptFrequency();
			int numberOfConceptInDocument = documentVector
					.getNumberOfSynsetInDocument();

			for (String string : conceptFrequency.keySet()) {
				double tf = (double) conceptFrequency.get(string)
						/ (double) numberOfConceptInDocument;
				termFrequencyFeature.put(string, tf);
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

	private void writeAllTermsOnFile(Map<String, Integer> frequencyMap)
			throws IOException {
		File outputFile = new File("output/allWords.txt");
		FileWriter fileWriter = new FileWriter(outputFile, true);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (String word : frequencyMap.keySet()) {
			bufferedWriter.write(word + "\n");
		}
		bufferedWriter.close();
	}

	private void writeNumberOfDocumentOnFile(int numberOfDocument) {
		try {
			Writer writer = new FileWriter("output/numberOfDocument.txt");
			writer.write(Integer.toString(numberOfDocument));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
