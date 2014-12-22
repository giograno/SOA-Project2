package text_analysis;

import rita.RiWordNet;

public class WordNetUtilities {

	private static final String WORDNET_PATH = "Wordnet-3/dict"; 
	
	private static RiWordNet wordNet = new RiWordNet("WordNet-3");
		
	/**
	 * Check existence of a word in WordNet vocabulary
	 * 
	 * @param aString
	 *            word to check
	 * @return true if word is in WordNet vocabulary; false otherwise
	 */
	public static boolean isInVocabulary(String aString) {
		return wordNet.exists(aString);
	}

	/**
	 * This method finds the most common part-of-speech for a word returning it
	 * for the version of the word with the most different senses
	 * 
	 * @param aString
	 *            the word to analyze
	 * @return A single-character String for the most common part of speech ('a'
	 *         = adjective, 'n' = noun, 'r' = adverb, 'v' = verb), or null if
	 *         the word is not found
	 */
	public static String getBestPos(String aString) {
		return wordNet.getBestPos(aString);
	}
	
	public static boolean isVerb(String aString){
		return wordNet.isVerb(aString);
	}

	/**
	 * This method gets from WordNet vocabulary the most relevant synset for
	 * given word
	 * 
	 * @param aString
	 *            the word to analyze
	 * @return a String for the most relevant synset found by WordNet query
	 */
	public static String getMostRelevantSynset(String aString) {
		return wordNet.getSynset(aString, getBestPos(aString))[0];
	}
}
