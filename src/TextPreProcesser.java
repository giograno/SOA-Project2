
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class TextPreProcesser {
	private String pdfTitle;
	private String pdfText;
	private Map<String, Integer> termFrequency = new HashMap<>();
	private static Map<String, Integer> documentFrequency = new HashMap<>();

	private final String STOP_WORDS = "stop-word";
	private final String OUTPUT_PATH = "intermediate-output";
	private String WN_PATH = "Wordnet-3/dict";

	public TextPreProcesser(String aTitle, String aText) {
		this.pdfTitle = aTitle;
		this.pdfText = aText;
	}

	public String removeStopWordAndStem() throws IOException {

		String localPath = "Wordnet-3/dict";
		URL url = new URL("file", null, localPath);

		IDictionary dict = new edu.mit.jwi.Dictionary(url);
		dict.open();
		WordnetStemmer stemmer = new WordnetStemmer(dict);

		StringBuilder sBuilder = new StringBuilder();
		try {
			List<String> stopWords = FileUtils.readLines(new File(STOP_WORDS));
			CharArraySet stopWSet = new CharArraySet(stopWords, true);

			TokenStream tokenStream = new LetterTokenizer(new StringReader(
					this.pdfText.trim()));
			tokenStream = new LowerCaseFilter(tokenStream);
			tokenStream = new StopFilter(tokenStream, stopWSet);

			CharTermAttribute charTermAttribute = tokenStream
					.addAttribute(CharTermAttribute.class);

			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String string = charTermAttribute.toString();

				String stemmedWord = getStem(stemmer, string);

				sBuilder.append(stemmedWord + " ");

				if (termFrequency.containsKey(stemmedWord)) {
					termFrequency.put(stemmedWord,
							termFrequency.get(stemmedWord) + 1);
				} else {
					termFrequency.put(stemmedWord, 1);
				}
			}
			tokenStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sBuilder.toString();
	}

	/**
	 * This method perform stemming of a given word. If it is possible find it,
	 * returns the stemmed word, otherwise returns the same word
	 * 
	 * @param stemmer
	 *            instance of WordnetStemmer
	 * @param aString
	 *            word to stem
	 * @return stemmed word or same input word
	 */
	private String getStem(WordnetStemmer stemmer, String aString) {
		List<String> possibleStems = stemmer.findStems(aString, null);

		if (!possibleStems.isEmpty()) {
			return possibleStems.get(0);
		} else {
			return aString;
		}
	}

	public Map<String, Integer> getTermFrequency() {
		return sortByValues(this.termFrequency);
	}

	private HashMap sortByValues(Map map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

}
