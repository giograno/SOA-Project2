import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.clapper.util.misc.FileHashMap;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.tagger.maxent.Dictionary;
import pdf_extractor.PDFExtractor;
import text_analysis.Stemmer;
import text_analysis.WordNetUtilities;

public class SequentialTFIDF {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException, ObjectExistsException, VersionMismatchException {

		/*
		 * input-files: for real data tfidf: small dummy test data
		 */
		final String INPUT_DIR = "input-files";
		final String FEATURE_OUTPUT = "features-vectors";

		TFIDF tfidf = new TFIDF(INPUT_DIR, FEATURE_OUTPUT);
		tfidf.extractFeatures();
	}
}
