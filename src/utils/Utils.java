package utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import utils.filter.PDFFilter;
import utils.filter.SerializedFilter;

public class Utils {

	public static File[] listPDFinDirectory(String directory) {
		File[] input_files = new File(directory).listFiles(new PDFFilter());
		return input_files;
	}

	public static File[] listSerializedInDirectory(String directory) {
		File[] temporary_files = new File(directory)
				.listFiles(new SerializedFilter());
		return temporary_files;
	}

	public static void cleanOrCreateDirectory(String directory) {
		File file = new File(directory);
		try {
			if (file.isDirectory()) {
				FileUtils.cleanDirectory(file);
			} else {
				FileUtils.forceMkdir(file);
			}
		} catch (IOException e) {
			System.err.println();
			e.printStackTrace();
		}
	}

	public static boolean isAscii(String string) {
		if (string == null) {
			return false;
		}

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch < 32 || ch > 122)
				return false;
		}
		return true;
	}
}
