package feature;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DocumentVector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int numberOfWordInDocument;
	private int numberOfSynsetsInDocument;
	private Map<String, Integer> termFrequency = new HashMap<String, Integer>();
	private Map<String, Integer> conceptFrequency = new HashMap<String, Integer>();

	/**
	 * Constructor for Term Frequency Inverse Document Frequency and Term
	 * Frequency feature
	 * 
	 * @param name
	 *            document name
	 * @param numberOfWordInDocument
	 *            total number of word in document
	 * @param numberOfSynsetsInDocument
	 *            total number of synonyms in document
	 * @param termFrequency
	 *            frequency Map for each word
	 * @param conceptFrequency
	 *            frequency Map for each concept
	 */
	public DocumentVector(String name, int numberOfWordInDocument,
			int numberOfSynsetsInDocument, Map<String, Integer> termFrequency,
			Map<String, Integer> conceptFrequency) {
		this.name = name;
		this.numberOfWordInDocument = numberOfWordInDocument;
		this.numberOfSynsetsInDocument = numberOfSynsetsInDocument;
		this.termFrequency = termFrequency;
		this.conceptFrequency = conceptFrequency;
	}

	/**
	 * Get the document name
	 * 
	 * @return a String with document name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get total number of word in document
	 * 
	 * @return number of total plain word
	 */
	public int getNumberOfWordInDocument() {
		return numberOfWordInDocument;
	}

	/**
	 * Get total number of synonyms in document
	 * 
	 * @return
	 */
	public int getNumberOfSynsetInDocument() {
		return numberOfSynsetsInDocument;
	}

	/**
	 * Get total number of word in document, including any synonyms
	 * 
	 * @return number of total word with synonyms
	 */
	public int getTotalNumberOfWord() {
		return numberOfWordInDocument + numberOfSynsetsInDocument;
	}

	/**
	 * Get occurrences for each term extracted
	 * 
	 * @return a Map with terms and their frequencies
	 */
	public Map<String, Integer> getTermFrequency() {
		return termFrequency;
	}

	/**
	 * Get occurrences for each synonym extracted
	 * 
	 * @return a Map with terms and their frequencies
	 */
	public Map<String, Integer> getConceptFrequency() {
		return conceptFrequency;
	}

}
