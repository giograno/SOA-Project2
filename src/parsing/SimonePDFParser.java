package parsing;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

public class SimonePDFParser {

	public PDFFile parse(File localPath) throws IOException, SAXException,
			TikaException {
		PDFFile pdfFile = new PDFFile();
		pdfFile.setLocalPath(localPath.toString());

		InputStream inputStream = new FileInputStream(localPath);
		ContentHandler contentHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		org.apache.tika.parser.pdf.PDFParser pdfParser = new org.apache.tika.parser.pdf.PDFParser();
		pdfParser.parse(inputStream, contentHandler, metadata,
				new ParseContext());

		pdfFile.setContent(contentHandler.toString());
		pdfFile.setMetadata(metadata);

		return pdfFile;
	}

}
