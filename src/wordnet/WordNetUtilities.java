package wordnet;

import rita.RiWordNet;

public class WordNetUtilities {

	private static RiWordNet wordNet = new RiWordNet("WordNet-3");

	public static boolean isInVocabulary(String pWord) {
		return wordNet.exists(pWord);
	}

	/**
	 * Finds the most common part-of-speech for a word based on its polysemy
	 * count, returning the pos for the version of the word with the most
	 * different senses
	 * 
	 * @param pWord
	 * @return A single-character String for the most common part of speech ('a'
	 *         = adjective, 'n' = noun, 'r' = adverb, 'v' = verb), or null if
	 *         the word is not found.
	 */
	public static String getBestPos(String pWord) {
		return wordNet.getBestPos(pWord);
	}
}
