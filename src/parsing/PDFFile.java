package parsing;

import org.apache.tika.metadata.Metadata;

public class PDFFile {
	private String localPath;
	private Metadata metadata;
	private String content;

	public PDFFile() {
		this.metadata = new Metadata();
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public String getFormattedMetadata() {
		String formatted = "";

		String[] names = metadata.names();

		for (String name : names) {
			formatted += name + ": ";
			if (metadata.isMultiValued(name)) {
				formatted += metadata.getValues(name).toString();
			} else {
				formatted += metadata.getValues(name)[0];
			}
			formatted += "\n";
		}

		return formatted;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
