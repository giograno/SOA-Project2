package text_analysis;

import org.tartarus.snowball.ext.EnglishStemmer;

public class Stemmer {

	/**
	 * Perform stemming of given a word. Control also the word obtained after
	 * stemming looking for it in WordNet vocabulary; if the result of stemming
	 * is not correct, try to look for most similar word
	 * 
	 * @param pString
	 *            word to stem
	 * @return word after stemming and validation process
	 */
	public static String stem(String pString) {
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(pString);
		stemmer.stem();
		String stemmedWord = stemmer.getCurrent();

		return stemmedWord;
//		if (WordNetUtilities.isInVocabulary(stemmedWord))
//			return stemmedWord;
//		else
//			return pString;
	}
}
