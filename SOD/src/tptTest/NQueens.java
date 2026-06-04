package tptTest;

import java.util.ArrayList;
import java.util.List;

public class NQueens {
	public List<List<String>> solveNQueens(int n) {
		List<List<String>> results = new ArrayList<>();
		backtrack(new int[n], 0, results);
		return results;
	}

	//check 1
	private void backtrack(int[] queens, int row, List<List<String>> results) {
		if (row == queens.length) {
			results.add(constructBoard(queens));
			return;
		}

		for (int col = 0; col < queens.length; col++) {
			if (isValid(queens, row, col)) {
				queens[row] = col; // Place queen
				backtrack(queens, row + 1, results); // Move to the next row
				// No need to undo since we overwrite on the next row
			}
		}
	}

	//Check 2
	private boolean isValid(int[] queens, int row, int col) {
		for (int r = 0; r < row; r++) {
			int c = queens[r];
			if (c == col || Math.abs(c - col) == Math.abs(r - row)) {
				return false; // Same column or diagonal
			}
		}
		return true;
	}

	
	private List<String> constructBoard(int[] queens) {
		List<String> board = new ArrayList<>();
		for (int i : queens) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < queens.length; j++) {
				sb.append(j == i ? "Q" : ".");
			}
			board.add(sb.toString());
		}
		return board;
	}

	public static void main(String[] args) {
		NQueens solution = new NQueens();
		int n = 4; // Example for 4-Queens
		List<List<String>> results = solution.solveNQueens(n);
		for (List<String> board : results) {
			for (String row : board) {
				System.out.println(row);
			}

			System.out.println();
		}
	}
}
