import feature.Feature;
import feature.TermFrequencyAndInverse;
import utils.Utils;

public class SequentialTFIDF {

	public static void main(String[] args) {

		final long startTime = System.currentTimeMillis();

		if (args.length != 2) {
			System.err
					.println("Give me the following arguments <input path> <output path>");
		}
		final String INPUT_DIR = args[0];
		final String OUTPUT_DIR = args[1];

		Utils.cleanOrCreateDirectory(OUTPUT_DIR);

		Feature feature = new TermFrequencyAndInverse(INPUT_DIR,
				OUTPUT_DIR);

		feature.extractFeatures();
		
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime));
	}
}
