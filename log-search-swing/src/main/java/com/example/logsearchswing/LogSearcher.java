package com.example.logsearchswing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogSearcher {

	private int currentLineNumber = 0;
	private String currentKeyword = null;
	private String[] currkeywords;
	private int keywordsLength = 0;
	private List<String> filedata;

	// ── Load file into memory ─────────────────────────────────────────────────
	public void loadData(String filePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			filedata = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				filedata.add(line);
			}
		} catch (IOException e) {
			System.out.println("loadData error: " + e.getMessage());
			filedata = new ArrayList<>();
		}
	}

	/**
	 * Full state reset. GUI clear() or new search when call this keywordsLength = 0
	 * → setDefaultValues() again initialize
	 */
	public void reset() {
		currentLineNumber = 0;
		currentKeyword = null;
		currkeywords = null;
		keywordsLength = 0;
		filedata = null;
	}

	// ── Internal: initialize keyword state once per search session ────────────
	private void setDefaultValues(String[] keyword) {
		if (keywordsLength == 0) {
			currkeywords = new String[keyword.length];
			for (int i = 0; i < keyword.length; i++) {
				currkeywords[i] = keyword[i].trim();
			}
			keywordsLength = keyword.length;
		}
	}

	// ── Find ALL matches for all keywords ─────────────────────────────────────
	public List<String> findAllMatch(String[] keyword) {
		List<String> results = new ArrayList<>();
		setDefaultValues(keyword);

		for (int i = 0; i < keywordsLength; i++) {
			if (currkeywords[i] == null)
				continue;

			currentKeyword = currkeywords[i];
			currkeywords[i] = null;
			currentLineNumber = 0;
			int matchCount = 0;

			for (String line : filedata) {
				currentLineNumber++;
				if (line.toLowerCase().contains(currentKeyword.toLowerCase())) {
					results.add("Match at Line " + currentLineNumber + ": " + line);
					matchCount++;
				}
			}
			if (matchCount == 0) {
				results.add("No more matches for keyword '" + currentKeyword + "'.");
			}
		}
		return results;
	}

	// ── Find NEXT single match (steps through keywords one by one) ────────────
	public String findNextMatch(String[] keyword) {
		setDefaultValues(keyword);

		for (int i = 0; i < keywordsLength; i++) {
			if (currkeywords[i] == null)
				continue;

			currentKeyword = currkeywords[i];
			currkeywords[i] = null;
			currentLineNumber = 0;

			for (String line : filedata) {
				currentLineNumber++;
				if (line.toLowerCase().contains(currentKeyword.toLowerCase())) {
					return "Match at Line " + currentLineNumber + ": " + line;
				}
			}
		}
		return null; // no match found
	}

	// ── Get next N context lines after last match ─────────────────────────────
	public List<String> getNextLines(String[] currentKeywords) {
		List<String> result = new ArrayList<>();

		if (filedata == null || currentLineNumber >= filedata.size()) {
			result.add("No more lines available.");
			return result;
		}

		int startIndex = currentLineNumber; // 0-based index of next line
		int maxLinePrint = startIndex + 20;

		for (int j = startIndex; j < filedata.size() && j < maxLinePrint; j++) {
			String line = filedata.get(j);
			// Stop if we hit another keyword match
			for (String kw : currentKeywords) {
				if (line.toLowerCase().contains(kw.trim().toLowerCase())) {
//                    result.add("Stopped at next match: Line " + (j + 1) + ": " + line);
					currentLineNumber = j + 1;
					return result;
				}
			}
			result.add("Next Line " + (j + 1) + ": " + line);
			currentLineNumber = j + 1;
		}

		if (result.isEmpty()) {
			result.add("No more lines available.");
		}
		return result;
	}
}