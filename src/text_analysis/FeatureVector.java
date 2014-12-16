package text_analysis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

public class FeatureVector {

	private String pdfTitle;
	private String pdfText;
	/* total words in document */
	private int wordCountDocument;
	private Map<String, Integer> termFrequency = new HashMap<>();

	/* stop-word file */
	private final String STOP_WORDS = "stop-word";
	/* location for temporary frequency vector */
	private final String OUTPUT_PATH = "intermediate-output";

	public FeatureVector(String pdfTitle, String pdfText) {
		this.pdfTitle = pdfTitle;
		this.pdfText = pdfText;
	}

	public FeatureVector() {
	}

	/*
	 * remember: this method have to work on pdfText variable class and not on
	 * any parameters
	 * 
	 * We can fill the HashMap directly in this method!
	 */
	public String removeStopWord(String pTextToParse) {
		StringBuilder sBuilder = new StringBuilder();
		try {
			// CharArraySet stopWSet = EnglishAnalyzer.getDefaultStopSet();
			List<String> stopWords = FileUtils.readLines(new File(STOP_WORDS));
			CharArraySet stopWSet = new CharArraySet(stopWords, true);

			TokenStream tokenStream = new StandardTokenizer(new StringReader(
					pTextToParse.trim()));
			tokenStream = new LowerCaseFilter(tokenStream);
			tokenStream = new StopFilter(tokenStream, stopWSet);

			CharTermAttribute charTermAttribute = tokenStream
					.addAttribute(CharTermAttribute.class);

			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String string = charTermAttribute.toString();
				// Here we do stemming
				sBuilder.append(Stemmer.stem(string) + " ");
				
				// Fill the HashMap
				
				wordCountDocument++;
			}
			tokenStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sBuilder.toString();
	}

}
