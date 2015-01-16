package utils;

import java.io.File;
import java.io.FilenameFilter;

public class SerializedFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if (name.endsWith(".ser"))
			return true;
		else
			return false;
	}

}
