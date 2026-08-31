package com.test.sod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CsvKeywordSearch {

	// ---- Modify these values as needed ----
	private static final String INPUT_DIR = "/home/dev021/Downloads/changlog/Changelog/";

	private static final String OUTPUT_DIR = "/home/dev021/Downloads/changlog/output files";

	private static final String KEYWORD = "5074753";
	private static final String TABLE_ID = "260";

	// Only files ending with this extension will be processed. Set to "" to process
	// all files.
	private static final String FILE_EXTENSION = ".csv";
	// -----------------------------------------

	public static void main(String[] args) {

		File inputFolder = new File(INPUT_DIR);
		File outputFolder = new File(OUTPUT_DIR);

		if (!outputFolder.exists()) {
			outputFolder.mkdirs();
		}

		File[] files = inputFolder.listFiles(
				(dir, name) -> FILE_EXTENSION.isEmpty() || name.toLowerCase().endsWith(FILE_EXTENSION.toLowerCase()));

		if (files == null || files.length == 0) {
			System.out.println("No matching files found in: " + INPUT_DIR);
			return;
		}

		int totalFilesScanned = 0;
		int filesWithMatches = 0;

		for (File file : files) {
			if (file.isFile()) {
				totalFilesScanned++;
				boolean hasMatch = processFile(file);
				if (hasMatch) {
					filesWithMatches++;
				}
			}
		}

		System.out.println("----------------------------------------");
		System.out.println("SUMMARY");
		System.out.println("Total files scanned : " + totalFilesScanned);
		System.out.println("Files with matches  : " + filesWithMatches);
		System.out.println("Files with no match : " + (totalFilesScanned - filesWithMatches));
		System.out.println("----------------------------------------");
	}

	// Returns true if at least one match was found (and output file kept)
	private static boolean processFile(File inputFile) {

		String fileName = inputFile.getName();
		int dotIndex = fileName.lastIndexOf('.');
		String baseName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);

		String outputFileName = baseName + "_matches.csv";
		File outputFile = new File(OUTPUT_DIR, outputFileName);

		int totalLines = 0;
		int matchedLines = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

			String line;
			while ((line = reader.readLine()) != null) {
				totalLines++;

				if (line.contains(KEYWORD)) {
					if (line.contains(TABLE_ID)) {
						writer.write(line);
						writer.newLine();
						matchedLines++;
					}
				}
			}

		} catch (IOException e) {
			System.err.println("Error processing file " + fileName + ": " + e.getMessage());
			return false;
		}

		if (matchedLines == 0) {
			// No matches found -> delete the empty output file
			outputFile.delete();
			System.out.println("Processed: " + fileName + " | Total lines: " + totalLines
					+ " | Matches: 0 (no output file created)");
		} else {
			System.out.println("Processed: " + fileName + " | Total lines: " + totalLines + " | Matches: "
					+ matchedLines + " | Output: " + outputFile.getPath());
		}

		return matchedLines > 0;
	}
}
