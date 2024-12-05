package net.anotheria.anosite.cms.bean;

/**
 * The bean used for the FileUploadDialog.
 */
public class UploadFileBean {
	private String name;
	private String size;
	private String message;
	private boolean filePresent;

	public boolean isFilePresent() {
		return filePresent;
	}

	public void setFilePresent(boolean filePresent) {
		this.filePresent = filePresent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
