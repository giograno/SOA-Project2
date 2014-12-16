package text_analysis;

import org.tartarus.snowball.ext.EnglishStemmer;

public class Stemmer {

	/**
	 * Perform stemming of a word; control also the word obtained after stemming
	 * looking for it in WordNet vocabulary
	 * 
	 * @param pString
	 *            word to stem
	 * @return word after stemming and validation process
	 */
	public static String stem(String pString) {
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(pString);
		stemmer.stem();
		if (WordNetUtilities.isInVocabulary(stemmer.getCurrent())) {
			return stemmer.getCurrent();
		} else {
			return pString;
		}
	}
}
