import java.io.IOException;

import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

public class SequentialTFIDF {

	public static void main(String[] args) throws ClassNotFoundException,
			IOException, ObjectExistsException, VersionMismatchException {

		/*
		 * input-files: for real data tfidf: small dummy test data
		 */
		final String INPUT_DIR = "tfidf";
		final String FEATURE_OUTPUT = "features-vectors";

		DocumentFeatures documentFeatures = new TFIDF(INPUT_DIR, FEATURE_OUTPUT); ;
		
//		TFIDF tfidf = new TFIDF(INPUT_DIR, FEATURE_OUTPUT);
//		tfidf.extractFeatures();
		
		documentFeatures.extractFeatures();
	}
}
