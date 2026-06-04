package com.logsearch.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogSearch {
	private int currentLineNumber = 0;
	private String currentKeyword = null;
	private String[] currkeywords;
	private long keywordsLength = 0;
	private List<String> filedata;

	public void loadData(String filePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			filedata = new ArrayList<>();
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				filedata.add(currentLine);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void setDefaultValues(String[] keyword) {
		if (keywordsLength == 0) {
			currkeywords = new String[keyword.length];
			for (int i = 0; i < keyword.length; i++) {
				currkeywords[i] = keyword[i].trim();
			}
			keywordsLength = keyword.length;
		}
	}

	// Find the next occurrence of the keyword
	public List<String> findAllMatch(String[] keyword) {
		List<String> results = new ArrayList<>();
		setDefaultValues(keyword);

		for (int i = 0; i < keywordsLength; i++) {
			currentLineNumber = 0;
			int currentresultcount = 0;
			if (currkeywords[i] == null) {
				continue;
			}
			currentKeyword = currkeywords[i];
			currkeywords[i] = null;

			for (String string : filedata) {
				currentLineNumber++;
				if (string.toLowerCase().contains(currentKeyword.toLowerCase())) {
					results.add("Match at Line " + currentLineNumber + ": " + string);
					currentresultcount++;
				}
			}
			if (currentresultcount == 0)
				results.add("No more matches for keyword '" + currentKeyword + "'.");
		}
		return results;
	}

	public String findNextMatch(String[] keyword) {

		setDefaultValues(keyword);
		String result = null;
		for (int i = 0; i < keywordsLength; i++) {

			if (currkeywords[i] == null) {
				continue;
			}
			currentKeyword = currkeywords[i];
			currkeywords[i] = null;

			System.out.println("Keyword : " + keyword[i] + " -> currentLineNumber : " + currentLineNumber);
			currentLineNumber = 0;

			for (String string : filedata) {
				currentLineNumber++;
				if (string.toLowerCase().contains(currentKeyword.toLowerCase())) {
					result = "Match at Line " + currentLineNumber + ": " + string;
					return result;
				}
			}

		}
		return null;
	}

	public List<String> getNextLines(String[] currentKeywords) {
		List<String> result = new ArrayList<>();

		int linesRead = 0;
		currentLineNumber++;
		int maxLinePrint = currentLineNumber + 20;
		for (int j = currentLineNumber - 1; j < filedata.size() && j < maxLinePrint; j++) {
			for (int i = 0; i < keywordsLength; i++) {
				currentKeyword = currentKeywords[i];

				if (filedata.get(j).toLowerCase().contains(currentKeyword.toLowerCase())) {
					return result;
				}
			}
			result.add("Next Line : " + currentLineNumber + ": " + filedata.get(j));

			currentLineNumber++;
		}

		return result;
	}
}