package feature;

import java.util.HashMap;
import java.util.Map;

public class NumberOfDocumentsWhereWordAppears {

	public static Map<String, Integer> numberOfDocumentsWhereWordAppears = new HashMap<String, Integer>();
	public static Map<String, Integer> numberOfDocumentsWhereConceptAppears = new HashMap<String, Integer>();

	public static void updateNumberOfDocumentsWhereWordAppears(String word) {

		if (numberOfDocumentsWhereWordAppears.containsKey(word)) {
			int termFrequency = numberOfDocumentsWhereWordAppears.get(word);
			numberOfDocumentsWhereWordAppears.put(word, termFrequency + 1);
		} else {
			numberOfDocumentsWhereWordAppears.put(word, 1);
		}
	}

	public static void removeWordFromDocumentCorpus(String word) {
		if (numberOfDocumentsWhereWordAppears.get(word) == 1) {
			numberOfDocumentsWhereWordAppears.remove(word);
		} else {
			int termFrequency = numberOfDocumentsWhereWordAppears.get(word);
			numberOfDocumentsWhereWordAppears.put(word, termFrequency - 1);
		}
	}

	public static Map<String, Integer> getNumberOfDocumentsWhereWordAppears() {
		return numberOfDocumentsWhereWordAppears;
	}

	/* CONCEPTS */

	public static void updateNumberOfDocumentsWhereConceptAppears(String word) {

		if (numberOfDocumentsWhereConceptAppears.containsKey(word)) {
			int termFrequency = numberOfDocumentsWhereConceptAppears.get(word);
			numberOfDocumentsWhereConceptAppears.put(word, termFrequency + 1);
		} else {
			numberOfDocumentsWhereConceptAppears.put(word, 1);
		}
	}

	public static void removeConceptFromDocumentCorpus(String word) {
		if (numberOfDocumentsWhereConceptAppears.get(word) == 1) {
			numberOfDocumentsWhereConceptAppears.remove(word);
		} else {
			int termFrequency = numberOfDocumentsWhereConceptAppears.get(word);
			numberOfDocumentsWhereConceptAppears.put(word, termFrequency - 1);
		}
	}

	public static Map<String, Integer> getNumberOfDocumentsWhereConceptAppears() {
		return numberOfDocumentsWhereConceptAppears;
	}

}
