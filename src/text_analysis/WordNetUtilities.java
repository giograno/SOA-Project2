package text_analysis;

import rita.RiWordNet;

public class WordNetUtilities {

	private static RiWordNet wordNet = new RiWordNet("WordNet-3");

	/**
	 * Check existence of a word in WordNet vocabulary
	 * 
	 * @param pWord
	 *            word to check
	 * @return true if word is in WordNet vocabulary; false otherwise
	 */
	public static boolean isInVocabulary(String pWord) {
		return wordNet.exists(pWord);
	}

	public static String[] getSynonms(String pWord) {
		return wordNet.getSynonyms(pWord, RiWordNet.NOUN);
	}
}
