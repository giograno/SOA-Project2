import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentVector {

	private String name;
	private int numberOfWordInDocument; // N
	private Map<String, Integer> termFrequency = new HashMap<String, Integer>();

	public DocumentVector(String name, int numberOfWordInDocument,
			Map<String, Integer> termFrequency) {
		this.name = name;
		this.numberOfWordInDocument = numberOfWordInDocument;
		this.termFrequency = termFrequency;
	}

	public String getName() {
		return name;
	}

	public int getNumberOfWordInDocument() {
		return numberOfWordInDocument;
	}

	public Map<String, Integer> getTermFrequency() {
		return termFrequency;
	}

}
