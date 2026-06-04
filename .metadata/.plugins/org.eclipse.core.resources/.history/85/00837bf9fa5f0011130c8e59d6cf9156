package com.workdrive.organizer.model;

/**
 * Represents a file/folder in Zoho WorkDrive.
 */
public class WorkDriveFile {

	private String id;
	private String name;
	private String extension;
	private String type; // "folder", "spreadsheet", "docs", etc.
	private boolean isFolder;
	private long sizeInBytes;
	private String modifiedTime;
	private String parentId;
	private String permalink;

	public WorkDriveFile() {
	}

	public WorkDriveFile(String id, String name, String extension, String type, boolean isFolder, long sizeInBytes,
			String modifiedTime, String parentId, String permalink) {
		this.id = id;
		this.name = name;
		this.extension = extension;
		this.type = type;
		this.isFolder = isFolder;
		this.sizeInBytes = sizeInBytes;
		this.modifiedTime = modifiedTime;
		this.parentId = parentId;
		this.permalink = permalink;
	}

	// ── Getters & Setters ──────────────────────────────────────────────────

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String ext) {
		this.extension = ext;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean folder) {
		this.isFolder = folder;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public void setSizeInBytes(long size) {
		this.sizeInBytes = size;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String t) {
		this.modifiedTime = t;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String pid) {
		this.parentId = pid;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String link) {
		this.permalink = link;
	}

	/**
	 * Determine target folder based on extension. Returns null if no rule matches.
	 */
	public String resolveTargetFolderKey() {
		if (isFolder)
			return null;
		if (extension == null || extension.isEmpty())
			return null;

		switch (extension.toLowerCase()) {
		case "xlsx":
		case "xls":
		case "csv":
		case "ods":
			return "sheet";
		case "docx":
		case "doc":
		case "odt":
		case "txt":
		case "rtf":
			return "writer";
		default:
			return null; // unrecognized — leave in place
		}
	}

	@Override
	public String toString() {
		return "WorkDriveFile [id=" + id + ", name=" + name + ", extension=" + extension + ", type=" + type
				+ ", isFolder=" + isFolder + ", sizeInBytes=" + sizeInBytes + ", modifiedTime=" + modifiedTime
				+ ", parentId=" + parentId + ", permalink=" + permalink + "]";
	}

	
}