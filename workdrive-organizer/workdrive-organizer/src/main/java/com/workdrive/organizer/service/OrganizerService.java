package com.workdrive.organizer.service;

import com.workdrive.organizer.model.WorkDriveFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Core business logic: 1. List all files in the main folder 2. Determine target
 * folder per file (by extension) 3. Move each file to its target folder
 */
public class OrganizerService {

	private static final Logger log = LoggerFactory.getLogger(OrganizerService.class);

	private final WorkDriveApiService apiService;
	private final String mainFolderId;
	private final String sheetFolderId;
	private final String writerFolderId;

	public OrganizerService(WorkDriveApiService apiService, String mainFolderId, String sheetFolderId,
			String writerFolderId) {
		this.apiService = apiService;
		this.mainFolderId = mainFolderId;
		this.sheetFolderId = sheetFolderId;
		this.writerFolderId = writerFolderId;
	}

	/**
	 * Runs the organizer and returns a result summary.
	 */
	public OrganizeResult organize() throws IOException {
//		log.trace("ENTER OrganizerService.organize()");
//		log.info("Starting organize for folder: {}", mainFolderId);

		List<WorkDriveFile> files = apiService.listFiles(mainFolderId);
		log.debug("Files to process: {}", files.size());

		OrganizeResult result = new OrganizeResult();
		result.setTotalScanned(files.size());

		for (WorkDriveFile file : files) {
			log.trace("Processing file: {} (ext={})", file.getName(), file.getExtension());

			String targetKey = file.resolveTargetFolderKey();
			log.debug("File: {} → targetKey: {}", file.getName(), targetKey);

			if (targetKey == null) {
				log.info("Skipping (no rule): {}", file.getName());
				result.addSkipped(file);
				continue;
			}

			String targetFolderId = resolveFolder(targetKey);
			log.trace("Resolved folder ID: {}", targetFolderId);

			boolean moved = apiService.moveFile(file.getId(), targetFolderId);
			if (moved) {
				log.info("✓ Moved: {} → {}", file.getName(), targetKey);
				result.addMoved(file, targetKey);
			} else {
				log.error("✗ Failed: {}", file.getName());
				result.addFailed(file);
			}
		}

		log.info("Organize complete — Moved={}, Skipped={}, Failed={}", result.getMovedCount(),
				result.getSkippedCount(), result.getFailedCount());
//		log.trace("EXIT OrganizerService.organize()");
		return result;
	}

	private String resolveFolder(String key) {
		switch (key) {
		case "sheet":
			return sheetFolderId;
		case "writer":
			return writerFolderId;
		default:
			throw new IllegalArgumentException("Unknown folder key: " + key);
		}
	}

	// ── Inner result class ─────────────────────────────────────────────────

	public static class OrganizeResult {
		private int totalScanned;
		private final List<MoveRecord> moved = new ArrayList<>();
		private final List<WorkDriveFile> skipped = new ArrayList<>();
		private final List<WorkDriveFile> failed = new ArrayList<>();

		public void addMoved(WorkDriveFile f, String targetKey) {
			moved.add(new MoveRecord(f, targetKey));
		}

		public void addSkipped(WorkDriveFile f) {
			skipped.add(f);
		}

		public void addFailed(WorkDriveFile f) {
			failed.add(f);
		}

		public int getTotalScanned() {
			return totalScanned;
		}

		public void setTotalScanned(int n) {
			this.totalScanned = n;
		}

		public List<MoveRecord> getMoved() {
			return moved;
		}

		public List<WorkDriveFile> getSkipped() {
			return skipped;
		}

		public List<WorkDriveFile> getFailed() {
			return failed;
		}

		public int getMovedCount() {
			return moved.size();
		}

		public int getSkippedCount() {
			return skipped.size();
		}

		public int getFailedCount() {
			return failed.size();
		}

		/** Converts result to JSON string for API response. */
		public String toJson() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("\"totalScanned\":").append(totalScanned).append(",");
			sb.append("\"moved\":").append(movedToJson()).append(",");
			sb.append("\"skipped\":").append(filesListToJson(skipped)).append(",");
			sb.append("\"failed\":").append(filesListToJson(failed));
			sb.append("}");
			return sb.toString();
		}

		private String movedToJson() {
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < moved.size(); i++) {
				MoveRecord r = moved.get(i);
				sb.append("{\"name\":\"").append(escape(r.file.getName())).append("\"").append(",\"id\":\"")
						.append(r.file.getId()).append("\"").append(",\"destination\":\"").append(r.targetKey)
						.append("\"").append(",\"extension\":\"").append(r.file.getExtension()).append("\"}");
				if (i < moved.size() - 1)
					sb.append(",");
			}
			return sb.append("]").toString();
		}

		private String filesListToJson(List<WorkDriveFile> list) {
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < list.size(); i++) {
				WorkDriveFile f = list.get(i);
				sb.append("{\"name\":\"").append(escape(f.getName())).append("\"").append(",\"id\":\"")
						.append(f.getId()).append("\"").append(",\"extension\":\"").append(f.getExtension())
						.append("\"}");
				if (i < list.size() - 1)
					sb.append(",");
			}
			return sb.append("]").toString();
		}

		private String escape(String s) {
			return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
		}
	}

	public static class MoveRecord {
		public final WorkDriveFile file;
		public final String targetKey;

		MoveRecord(WorkDriveFile file, String targetKey) {
			this.file = file;
			this.targetKey = targetKey;
		}
	}
}
