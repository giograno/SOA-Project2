package posTagger;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSutils {

	private static String[] s_stopwords = { "DT", "CD", "CC", "EX", "IN", "MD",
			"PDT", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "POS", "SYM", "TO",
			"UH", "WP", "WP$", "WRB", "WDT", "#", "$", "\"", "(", ")", ",",
			".", ":", "''", "LRB", "RRB", "LCB", "RCB", "LSB", "RSB", "-LRB-",
			"B-", "``", "FW", "-RRB-", " ", " " };

	private HashSet<String> stopwords;
	private Morphology morphology = new Morphology();

	private Map<String, Integer> termFrequency = new HashMap<>();

	public POSutils() {
		stopwords = new HashSet<String>(s_stopwords.length);
		for (String stopWord : s_stopwords) {
			stopwords.add(stopWord);
		}
	}

	public String getLemmatization(String document) {

		document = tagText(document);

		String[] splittedDocument = document.split(" ");
		String output = "";

		for (int i = 0; i < splittedDocument.length; i++) {
			if (!isStopWord(splittedDocument[i].split("/")[1])) {

				WordLemmaTag lemma = morphology.lemmatize(morphology.stem(
						splittedDocument[i].split("/")[0],
						splittedDocument[i].split("/")[1]));
				if (!lemma.value().equalsIgnoreCase("be")
						&& !lemma.value().equalsIgnoreCase("have")) {
					String stemmedWord = lemma.lemma().toLowerCase();

					output += stemmedWord + " ";

					if (termFrequency.containsKey(stemmedWord)) {
						termFrequency.put(stemmedWord,
								termFrequency.get(stemmedWord) + 1);
					} else {
						termFrequency.put(stemmedWord, 1);
					}
				}
			}
		}
		return output;
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

	/**
	 * This method apply POS tag to a given string
	 * 
	 * @param stringToTag
	 *            string to tag
	 * @return a String with POS tag applied
	 */
	private static String tagText(String stringToTag) {
		MaxentTagger tagger = null;
		try {
			tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		String stringTagged = tagger.tagString(stringToTag);
		return stringTagged;
	}

	/**
	 * Check if a given word contains an invalid POS tag
	 * 
	 * @param aWord
	 *            word to check
	 * @return true if word contains a stop word tag, false otherwise
	 */
	private boolean isStopWord(String aWord) {
		return stopwords.contains(aWord);
	}
}
