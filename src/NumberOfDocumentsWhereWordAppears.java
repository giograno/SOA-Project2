import java.util.HashMap;
import java.util.Map;

public class NumberOfDocumentsWhereWordAppears {

	static Map<String, Integer> numberOfDocumentsWhereWordAppears = new HashMap<String, Integer>();

	public static void updateNumberOfDocumentsWhereWordAppears(String word) {
		int counter = numberOfDocumentsWhereWordAppears.containsKey(word) ? numberOfDocumentsWhereWordAppears
				.get(word) : 0;
		numberOfDocumentsWhereWordAppears.put(word, counter + 1);
	}
}
