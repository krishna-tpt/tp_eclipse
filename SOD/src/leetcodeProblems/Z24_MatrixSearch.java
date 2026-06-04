package leetcodeProblems;

import java.util.Scanner;

public class Z24_MatrixSearch {
	/**
	 * Search in a row-sorted, column-sorted M×N matrix. Strategy: Start from
	 * top-right corner. - If current == target → found - If current > target → move
	 * left (eliminate column) - If current < target → move down (eliminate row)
	 * Time: O(M+N), Space: O(1)
	 */
	public static int[] search(int[][] matrix, int target) {
		int rows = matrix.length;
		int cols = matrix[0].length;

		int row = 0;
		int col = cols - 1; // start at top-right

		while (row < rows && col >= 0) {
			if (matrix[row][col] == target) {
				return new int[] { row, col };
			} else if (matrix[row][col] > target) {
				col--; // too big → go left
			} else {
				row++; // too small → go down
			}
		}
		return null; // not found
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// --- Read matrix dimensions ---
/*		System.out.print("Enter number of rows (M): ");
		int m = Integer.parseInt(scanner.nextLine().trim());

		System.out.print("Enter number of columns (N): ");
		int n = Integer.parseInt(scanner.nextLine().trim());

		int[][] matrix = new int[m][n];

		// --- Read matrix values ---
		System.out.println("Enter matrix row by row (space-separated values):");
		for (int i = 0; i < m; i++) {
			System.out.printf("  Row %d: ", i);
			String[] parts = scanner.nextLine().trim().split("\\s+");
			for (int j = 0; j < n; j++) {
				matrix[i][j] = Integer.parseInt(parts[j]);
			}
		}

		// --- Print the matrix ---
		System.out.println("\nMatrix:");
		int maxLen = 0;
		for (int[] row : matrix)
			for (int val : row)
				maxLen = Math.max(maxLen, String.valueOf(val).length());

		for (int[] row : matrix) {
			System.out.print("  ");
			for (int j = 0; j < n; j++) {
				System.out.printf("%" + (maxLen + 2) + "d", row[j]);
			}
			System.out.println();
		}

		// --- Read target ---
		System.out.print("\nEnter target value: ");
		int target = Integer.parseInt(scanner.nextLine().trim()); */
		
		int[][] matrix = {{1, 3, 5},
						  {4, 8, 9},
						  {13, 15, 20}};
		int target= 8;
		// --- Search ---
		int[] result = search(matrix, target);

		if (result != null) {
			System.out.printf("%nFound at (%d, %d)%n", result[0], result[1]);
		} else {
			System.out.printf("%nError: %d not found in the matrix.%n", target);
		}

		scanner.close();
	}
}