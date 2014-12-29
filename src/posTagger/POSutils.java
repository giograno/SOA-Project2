package posTagger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
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
	private Map<String, String> partOfSpeechMap = new HashMap<>();
	private Map<String, Integer> synsetFrequency = new HashMap<>();
	private Map<String, Integer> featuresVector = new HashMap<>();

	private static final String OUTPUT_PATH = "word-count-freq/word-count-freq.txt";

	private BufferedWriter bufferedWriter;

	public POSutils() {
		stopwords = new HashSet<String>(s_stopwords.length);
		for (String stopWord : s_stopwords) {
			stopwords.add(stopWord);
		}
	}

	public String getLemmatization(String pFileName, String document)
			throws IOException {

		/* aggiungiamo lo stemmer */

		String localPath = "Wordnet-3/dict";
		URL url = new URL("file", null, localPath);

		IDictionary dict = new edu.mit.jwi.Dictionary(url);
		dict.open();
		WordnetStemmer stemmer = new WordnetStemmer(dict);

		/* ------ */

		document = tagText(document);

		String[] splittedDocument = document.split(" ");
		String output = "";

		WordTag wordWithTag;
		String originalWord, pos;
		for (int i = 0; i < splittedDocument.length; i++) {
			if (!isStopWord(splittedDocument[i].split("/")[1])) {

				pos = splittedDocument[i].split("/")[1];
				originalWord = splittedDocument[i].split("/")[0];
				// this is the word tag (String word, String tag)
				wordWithTag = getStem(stemmer, originalWord, pos);

				WordLemmaTag lemma = morphology.lemmatize(wordWithTag);
				if (!lemma.value().equalsIgnoreCase("be")
						&& !lemma.value().equalsIgnoreCase("have")) {
					String stemmedWord = lemma.lemma().toLowerCase();

					output += stemmedWord + " ";
					// Store word with relative POS tag
					storePOS(stemmedWord, pos);

					if (termFrequency
							.containsKey(stemmedWord + "@" + pFileName)) {
						termFrequency
								.put(stemmedWord + "@" + pFileName,
										termFrequency.get(stemmedWord + "@"
												+ pFileName) + 1);
					} else {
						termFrequency.put(stemmedWord + "@" + pFileName, 1);
					}
				}
			}
		}

		this.addConceptVector(dict, pFileName);
		// aggiungiamo i vettori
		featuresVector.putAll(termFrequency);
		featuresVector.putAll(synsetFrequency);
		writeOnFile();
		return output;
	}

	private void storePOS(String pWord, String pPos) {
		if (!partOfSpeechMap.containsKey(pWord)) {
			partOfSpeechMap.put(pWord, pPos);
		}
	}

	public void addConceptVector(IDictionary dictionary, String pFileName) {
		String wordSynset;
		String onlyWord;
		POS pos;
		int frequency;
		for (String aWord : this.termFrequency.keySet()) {
			onlyWord = aWord.split("@")[0];
			pos = getPos(partOfSpeechMap.get(onlyWord));
			frequency = this.termFrequency.get(aWord);
			wordSynset = getSynset(dictionary, onlyWord, pos);
			if (wordSynset != null) {
				this.synsetFrequency.put(wordSynset + "@" + pFileName,
						frequency);
			}
		}
	}

	public Map<String, Integer> getTermFrequency() {
		return sortByValues(this.featuresVector);
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

	private WordTag getStem(WordnetStemmer stemmer, String aString, String pos) {
		List<String> possibleStems = stemmer.findStems(aString, getPos(pos));

		if (!possibleStems.isEmpty()) {
			String word = possibleStems.get(0);
			return new WordTag(word, pos);
		} else {
			return new WordTag(aString, pos);
		}
	}

	private String getSynset(IDictionary dictionary, String word, POS pos) {
		IIndexWord indexWord = dictionary.getIndexWord(word, pos);
		String synonym;
		int synsetSize;
		if (indexWord != null) {
			IWordID wordID = indexWord.getWordIDs().get(0);
			IWord iWord = dictionary.getWord(wordID);
			ISynset synset = iWord.getSynset();

			synsetSize = synset.getWords().size();
			for (int i = 0; i < synsetSize; i++) {
				synonym = synset.getWords().get(i).getLemma();
				if (!synonym.equals(word)) {
					return synonym;
				}
			}
			// no different synset found
			return null;
		} else {
			return null;
		}
	}

	private POS getPos(String pos) {
		if (pos.equals("NN") || pos.equals("NNS") || pos.equals("NNP")
				|| pos.equals("NNPS")) {
			return POS.NOUN;
		} else if (pos.equals("VB") || pos.equals("VBD") || pos.equals("VBG")
				|| pos.equals("VBN") || pos.equals("VBP") || pos.equals("VBZ")) {
			return POS.VERB;
		} else if (pos.equals("JJ") || pos.equals("JJR") || pos.equals("JJS")) {
			return POS.ADJECTIVE;
		}
		return null;
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

	private void writeOnFile() {

		try {
			File file = new File(OUTPUT_PATH);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file, true);
			bufferedWriter = new BufferedWriter(fileWriter);

			
			for (String record : this.featuresVector.keySet()) {
				bufferedWriter.write(record + "\t"
						+ this.featuresVector.get(record) + "\n");
			}
			bufferedWriter.close();

			System.out
					.println("Ok! Successfully appended entire vector!!!\nYou are the best my friend!");

		} catch (IOException e) {
			System.err.println("AHAHAHHAHAH PROBLEM!!!");
			e.printStackTrace();
		}
	}

}
