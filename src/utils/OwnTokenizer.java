package utils;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;

public class OwnTokenizer extends Tokenizer {

	protected OwnTokenizer(Reader input) {
		super(input);
	}

	@Override
	public boolean incrementToken() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}
