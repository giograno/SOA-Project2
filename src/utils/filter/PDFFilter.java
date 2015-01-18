package utils.filter;

import java.io.File;
import java.io.FilenameFilter;

public class PDFFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if (name.endsWith(".pdf"))
			return true;
		else
			return false;
	}

}
