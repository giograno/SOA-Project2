package feature;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DocumentVector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int numberOfWordInDocument;
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
