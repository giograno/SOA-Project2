import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pdf_extractor.PDFExtractor;
import text_analysis.FeatureVector;
import text_analysis.WordNetUtilities;

public class FirstPhase {

	public static void main(String[] args) {

		String inputDirectory = "input-files";

		File[] files = new File(inputDirectory).listFiles();
		int i = 0;
		for (File file : files) {
			if (i >= 1)
				break;
			PDFExtractor pdfExtractor = new PDFExtractor();
			String result = pdfExtractor.extractTextFromPDFDocument(file);
			// System.out.println(file.getName());
//			System.out.println(result);
			FeatureVector featureVector = new FeatureVector();
			String result_two = featureVector.removeStopWord(result);
//			System.out.println(result_two);
			System.out.println("*****************");
			i++;
			Map<String, Integer> vector = featureVector.getTermFrequency();
			for(Map.Entry<String, Integer> entry : vector.entrySet()){
				System.out.println(entry.getKey()+" : "+entry.getValue());
			}
		}
		
		
//		 String stringa =
//		 "This is my, files? and I have to try- is this word | x count worker correctly 9 9 9@ciao.me";
//		 FeatureVector fVector = new FeatureVector("frase", stringa);
//		 System.out.println(fVector.removeStopWord(stringa));

	}

}
