import text_analysis.FeatureVector;
import text_analysis.WordNetUtilities;

public class FirstPhase {

	public static void main(String[] args) {
		String stringa = "This is my, files? and I have to try- is this word | x count worker correctly 9 9 9@ciao.me";
		FeatureVector fVector = new FeatureVector("frase", stringa);
		System.out.println(fVector.removeStopWord(stringa));

	}

}
