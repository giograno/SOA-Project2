import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.tika.exception.TikaException;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;
import org.xml.sax.SAXException;

import parsing.PDFFile;
import parsing.SimonePDFParser;
import utils.Utils;

public class SequentialTFIDF {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.err
					.println("Give me the following arguments <input path> <output path>");
		}
		final String INPUT_DIR = args[0];
		final String OUTPUT_DIR = args[1];

		Utils.cleanOrCreateDirectory(OUTPUT_DIR);
		
		DocumentFeatures documentFeatures = new TFIDF(INPUT_DIR, OUTPUT_DIR);

		documentFeatures.extractFeatures();
		System.out.println("FINISH!!!!!!!!");

		// String input = "onlyThis";
		// File[] input_files = new File(input).listFiles(new FilenameFilter() {
		// public boolean accept(File dir, String name) {
		// if (name.endsWith(".pdf"))
		// return true;
		// else
		// return false;
		// }
		// });
		//
		// SimonePDFParser parser = new SimonePDFParser();
		// for (File file : input_files) {
		// try {
		// PDFFile pFile = parser.parse(file);
		// System.out.println("----------METADATA---------");
		// System.out.println(pFile.getMetadata());
		// System.out.println("----------CONTENT---------");
		// System.out.println(pFile.getContent());
		// } catch (Exception e) {
		// System.err.println("General Exception");
		// e.printStackTrace();
		// }
		// }

	}
}
