package utils;
public class Constants {

	// cut based on frequency threshold in single document
	public final static boolean CUT_ON_FREQUENCY = true;
	// lower threshold for cut
	public final static double LOWER_LIMIT = 0.2;
	// upper threshold for cut
	public final static double UPPER_LIMIT = 15;
	// maximum number of synonyms for each word
	public final static int MAXIMUM_SYNSETS_NUMBER = 1;
	// cut based on frequency threshold all over documents corpus
	public final static boolean CUT_ON_TOTAL_DOC = true;
}
