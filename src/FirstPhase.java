import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.Dictionary;
import pdf_extractor.PDFExtractor;
import posTagger.POSutils;
import text_analysis.TextPreProcesser;
import text_analysis.Stemmer;
import text_analysis.WordNetUtilities;

public class FirstPhase {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {

		String inputDirectory = "input-files";
		TextPreProcesser processer;

		File[] files = new File(inputDirectory).listFiles();
		int i = 0;
		for (File file : files) {
			if (i >= 1)
				break;
			String textExtracted = PDFExtractor
					.extractTextFromPDFDocument(file);
			 processer = new TextPreProcesser(file.toString(),textExtracted);
			 String result_two = processer.removeStopWordAndStem();
			 System.out.println(result_two);
//			 i++;
			 Map<String, Integer> vector = processer.getTermFrequency();
			 for(Map.Entry<String, Integer> entry : vector.entrySet()){
			 System.out.println(entry.getKey()+" : "+entry.getValue());
			 }
			System.out.println("****************************************");
			System.out.println("****************************************");
			System.out.println("****************************************");
			System.out.println("****************************************");

			i++;
			
			POSutils poSutils = new POSutils();
			String result = poSutils.getLemmatization(PDFExtractor.processingText(textExtracted));
			Map<String, Integer> vMap = poSutils.getTermFrequency();
			for (Map.Entry<String, Integer> entry : vMap.entrySet()) {
				System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		}

		String stringa = "This is my, files? Categorization computerizing recommend Mexican fitted categories ls Hadoop and I have to try- is this 1000 20 fishing fisher fished word | x count worker correctly 9 9 9@ciao.me";
		TextPreProcesser fVector = new TextPreProcesser("frase", stringa);
		fVector.removeStopWordAndStem();
		System.out.println("Letter Tokenizer: "
				+ fVector.removeStopWordAndStem());
		POSutils poSutils = new POSutils();
		System.out.println(poSutils.getLemmatization(stringa));

		String wnhome = System.getenv("WordNet-3");
		String path = wnhome + File.pathSeparator + "dict";
		String localPath = "Wordnet-3/dict";
		URL url = new URL("file", null, localPath);

		IDictionary dict = new edu.mit.jwi.Dictionary(url);
		dict.open();
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		System.out.println(stemmer.findStems("works", null));
		IIndexWord idxWord = dict.getIndexWord("have", POS.VERB);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		System.out.println("Lemma = " + word.getLemma());
		System.out.println("Gloss = " + word.getSynset().getGloss());

	}

}
