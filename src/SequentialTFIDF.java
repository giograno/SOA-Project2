import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

public class SequentialTFIDF {

	public static void main(String[] args) {

		/*
		 * input-files: for real data tfidf: small dummy test data
		 */

		if (args.length != 2) {
			System.err
					.println("Give me the following arguments <input path> <output path>");
		}
		final String INPUT_DIR = args[0];
		final String OUTPUT_DIR = args[1];

		File file = new File(OUTPUT_DIR);
		try {
			if (file.isDirectory()) {
				FileUtils.cleanDirectory(file);
			}else{
				FileUtils.forceMkdir(file);
			}
		} catch (IOException e) {
			System.err.println();
			e.printStackTrace();
		}

		DocumentFeatures documentFeatures = new TFIDF(INPUT_DIR, OUTPUT_DIR);

		// TFIDF tfidf = new TFIDF(INPUT_DIR, FEATURE_OUTPUT);
		// tfidf.extractFeatures();

		documentFeatures.extractFeatures();
	}
}
