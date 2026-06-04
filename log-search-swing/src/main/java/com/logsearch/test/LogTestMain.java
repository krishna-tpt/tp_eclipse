package com.logsearch.test;

import java.util.ArrayList;
import java.util.List;

public class LogTestMain {
	private final static LogSearch logSearcher = new LogSearch();
	private static String currentFilePath;
	private String currKeyword;
	private static int currentContextLines;
	private static String[] currentKeywords;
	private static boolean isAllMatch = true;
	private static int totalMatches = 0;

//	public static void main(String[] args) {
//		isAllMatch = false;
//		String keyword = "1452395, 11588601,11588610";
//		currentKeywords = keyword.split(",");
//		currentFilePath = "/home/dev021/Downloads/Changlog/changelog_dump_26112025_test.csv";
//		logSearcher.loadData(currentFilePath);
//		nextSearch();
//
//	}

	private static void nextSearch() {
		List<String> results = null;
		if (isAllMatch) {
			results = logSearcher.findAllMatch(currentKeywords);
//			logSearcher.closeFile();
		} else {
			results = new ArrayList<>();
			results.add(logSearcher.findNextMatch(currentKeywords));

			results.add(logSearcher.findNextMatch(currentKeywords));
			
			List<String> result = nextLines();
			for (String string : result) {
				results.add(string);
			}
//			System.out.println(results);
			results.add(logSearcher.findNextMatch(currentKeywords));
//			System.out.println(results);
//			results.add(logSearcher.findNextMatch(currentKeywords));
//			System.out.println(results);
		}

		long hits = results.stream().filter(r -> r.startsWith("Match at Line")).count();
		totalMatches += hits;
		System.out.println("totalMatches : " + totalMatches);

		for (String string : results) {
			System.out.println(string);
		}
	}

	private static List<String> nextLines() {
		System.out.println("calling nextlines method");
		List<String> results = logSearcher.getNextLines(currentKeywords);
//		logSearcher.closeFile();

//		if (results.isEmpty() || results.get(0).startsWith("No more lines") || results.get(0).startsWith("Error")
//				|| results.get(0).startsWith("No file")) {
//		System.out.println("No more lines");
//		} else if (results.get(0).startsWith("Stopped at next match")) {
//			System.out.println("Stopped — next keyword match encountered.");
//		} else {
//			System.out.println("Showing context lines.");
		return results;
	}
}
