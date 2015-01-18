import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class FeaturesExtractor {

	private HashSet<String> stopwords;
	private static String[] s_stopwords = { "DT", "CD", "CC", "EX", "IN", "MD",
			"PDT", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "POS", "SYM", "TO",
			"UH", "WP", "WP$", "WRB", "WDT", "#", "$", "\"", "(", ")", ",",
			".", ":", "''", "LRB", "RRB", "LCB", "RCB", "LSB", "RSB", "-LRB-",
			"B-", "``", "FW", "-RRB-", " ", " " };

	private String documentName;
	private int totalNumberOfWordsInDocument = 0;

	private Morphology morphology = new Morphology();
	private MaxentTagger tagger;
	private final String WORDNET_PATH = "Wordnet-3/dict";

	private Map<String, Integer> termFrequency = new HashMap<>();
//	private Map<String, Integer> synsetFrequency = new HashMap<>();
//	private Map<String, Integer> featuresVector = new HashMap<>();

//	private Map<String, String> partOfSpeechMap = new HashMap<>();

	public FeaturesExtractor(String pDocumentName, MaxentTagger tagger) {

		stopwords = new HashSet<String>(s_stopwords.length);
		for (String stopWord : s_stopwords) {
			stopwords.add(stopWord);
		}
		this.documentName = pDocumentName;
		this.tagger = tagger;
	}

	// public DocumentVector getDocumentVector(String document) throws
	// IOException {
	//
	// String localPath = "Wordnet-3/dict";
	// URL url = new URL("file", null, localPath);
	//
	// IDictionary dict = new edu.mit.jwi.Dictionary(url);
	// dict.open();
	// WordnetStemmer stemmer = new WordnetStemmer(dict);
	//
	// document = tagText(document);
	//
	// String[] splittedDocument = document.split(" ");
	//
	// WordTag wordWithTag;
	// String originalWord, pos;
	//
	// for (int i = 0; i < splittedDocument.length; i++) {
	// String[] temp = splittedDocument[i].split("/");
	// originalWord = temp[0].toLowerCase();
	// pos = temp[1];
	//
	// if (!isStopWord(pos) && isAscii(originalWord)) {
	//
	// // this is the word tag (String word, String tag)
	// wordWithTag = getStem(stemmer, originalWord, pos);
	//
	// WordLemmaTag lemma = morphology.lemmatize(wordWithTag);
	// if (!lemma.value().equalsIgnoreCase("be")
	// && !lemma.value().equalsIgnoreCase("have")) {
	// String stemmedWord = lemma.lemma().toLowerCase();
	//
	// // Store word with relative POS tag
	// storePOS(stemmedWord, pos);
	//
	// // also increment document
	// if (termFrequency.containsKey(stemmedWord)) {
	// termFrequency.put(stemmedWord,
	// termFrequency.get(stemmedWord) + 1);
	// totalNumberOfWordsInDocument++;
	// } else {
	// termFrequency.put(stemmedWord, 1);
	// totalNumberOfWordsInDocument++;
	//
	// NumberOfDocumentsWhereWordAppears
	// .updateNumberOfDocumentsWhereWordAppears(stemmedWord);
	// }
	// }
	// }
	// }
	//
	// if (Properties.CUT_ON_FREQUENCY) {
	// int threshold = (int) Math.round(Properties.LOWER_LIMIT
	// * totalNumberOfWordsInDocument / 100);
	// for (Iterator<Map.Entry<String, Integer>> iterator = this.termFrequency
	// .entrySet().iterator(); iterator.hasNext();) {
	// Map.Entry<String, Integer> entry = iterator.next();
	//
	// if (entry.getValue() < threshold) {
	// iterator.remove();
	// }
	// }
	// }
	// this.addConceptVector(dict);
	//
	// featuresVector.putAll(termFrequency);
	// featuresVector.putAll(synsetFrequency);
	//
	// dict.close();
	// return new DocumentVector(documentName, totalNumberOfWordsInDocument,
	// featuresVector);
	// }

	private boolean isAscii(String string) {
		if (string == null) {
			return false;
		}

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch < 32 || ch > 122)
				return false;
		}
		return true;
	}

	public DocumentVector getDocumentVectorV2(String document)
			throws IOException {

		URL url = new URL("file", null, WORDNET_PATH);
		IDictionary dict = new edu.mit.jwi.Dictionary(url);
		dict.open();

		document = tagger.tagString(document);

		String[] splittedDocument = document.split(" ");

		String originalWord, pos;

		for (int i = 0; i < splittedDocument.length; i++) {
			String[] temp = splittedDocument[i].split("/");
			originalWord = temp[0].toLowerCase();
			pos = temp[1];

			if (!isStopWord(pos)) {

				pos = splittedDocument[i].split("/")[1];
				originalWord = splittedDocument[i].split("/")[0].toLowerCase();

				WordLemmaTag lemma = morphology.lemmatize(morphology.stem(
						originalWord, pos));

				String stemmedWord = lemma.lemma().toLowerCase();

				if (termFrequency.containsKey(stemmedWord)) {
					termFrequency.put(stemmedWord,
							termFrequency.get(stemmedWord) + 1);
					totalNumberOfWordsInDocument++;
				} else {
					termFrequency.put(stemmedWord, 1);
					totalNumberOfWordsInDocument++;

					NumberOfDocumentsWhereWordAppears
							.updateNumberOfDocumentsWhereWordAppears(stemmedWord);
				}

				ArrayList<String> wordSynset = getSynsets(dict, originalWord,
						getPos(pos));
				if (wordSynset != null) {
					for (String string : wordSynset) {
						if (this.termFrequency.containsKey(string)) {
							this.termFrequency.put(string,
									this.termFrequency.get(string) + 1);
						} else {
							this.termFrequency.put(string, 1);
						}
						totalNumberOfWordsInDocument++;
						NumberOfDocumentsWhereWordAppears
								.updateNumberOfDocumentsWhereWordAppears(string);
					}
				}

			}
		}

		if (Properties.CUT_ON_FREQUENCY) {
			int threshold = (int) Math.round(Properties.LOWER_LIMIT
					* totalNumberOfWordsInDocument / 100);
			for (Iterator<Map.Entry<String, Integer>> iterator = this.termFrequency
					.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, Integer> entry = iterator.next();

				if (entry.getValue() < threshold) {
					iterator.remove();
				}
			}
		}

		dict.close();
		return new DocumentVector(documentName, totalNumberOfWordsInDocument,
				termFrequency);
	}

	// private void storePOS(String pWord, String pPos) {
	// if (!partOfSpeechMap.containsKey(pWord)) {
	// partOfSpeechMap.put(pWord, pPos);
	// }
	// }

//	public void addConceptVector(IDictionary dictionary) {
//		ArrayList<String> wordSynset;
//		POS pos;
//		int frequency;
//
//		for (String word : this.termFrequency.keySet()) {
//			pos = getPos(partOfSpeechMap.get(word));
//			frequency = this.termFrequency.get(word);
//			// We call private method getSynset
//			wordSynset = getSynsets(dictionary, word, pos);
//
//			if (wordSynset != null) {
//				for (String string : wordSynset) {
//					if (this.synsetFrequency.containsKey(string)) {
//						this.synsetFrequency.put(string,
//								this.synsetFrequency.get(string) + frequency);
//					} else {
//						this.synsetFrequency.put(string, frequency);
//					}
//					// it is necessary increment total number of words in
//					// document also for concept vectors?
//					totalNumberOfWordsInDocument++;
//					NumberOfDocumentsWhereWordAppears
//							.updateNumberOfDocumentsWhereWordAppears(string);
//				}
//			}
//		}
//	}

	// private static String tagText(String stringToTag) {
	// MaxentTagger tagger = null;
	// try {
	// tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
	// } catch (ClassNotFoundException | IOException e) {
	// e.printStackTrace();
	// }
	// String stringTagged = tagger.tagString(stringToTag);
	// return stringTagged;
	// }

	// private WordTag getStem(WordnetStemmer stemmer, String word, String pos)
	// {
	// List<String> possibleStems = stemmer.findStems(word, getPos(pos));
	//
	// if (!possibleStems.isEmpty()) {
	// String stemmedWord = possibleStems.get(0);
	// return new WordTag(stemmedWord, pos);
	// } else {
	// return new WordTag(word, pos);
	// }
	// }

	private ArrayList<String> getSynsets(IDictionary dictionary, String word,
			POS pos) {
		ArrayList<String> allSynsets = new ArrayList<>();
		IIndexWord indexWord = dictionary.getIndexWord(word, pos);
		String synonym;
		int synsetSize;

		if (indexWord != null) {
			IWordID wordID = indexWord.getWordIDs().get(0);
			IWord iWord = dictionary.getWord(wordID);
			ISynset synset = iWord.getSynset();

			synsetSize = synset.getWords().size();
			for (int i = 0; i < synsetSize
					&& i < Properties.MAXIMUM_SYNSETS_NUMBER; i++) {
				synonym = synset.getWords().get(i).getLemma();
				if (!synonym.equals(word)) {
					allSynsets.add(synonym);
				}
			}
			return allSynsets;
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

	private boolean isStopWord(String aWord) {
		return stopwords.contains(aWord);
	}

}
