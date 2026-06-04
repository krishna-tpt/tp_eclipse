package leetcodeProblems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Z24_TwoPointerPairs {
	/**
     * Find all unique pairs in a sorted array that sum to target.
     * Two Pointer approach: O(N) time, O(1) space
     *
     * - left starts at beginning, right at end
     * - If sum == target → record pair, skip duplicates on both sides
     * - If sum < target  → move left pointer right
     * - If sum > target  → move right pointer left
     */
	public static List<int[]> findPairs(int[] arr, int target) {
		List<int[]> result = new ArrayList<>();
		int left = 0;
		int right = arr.length - 1;

		while (left < right) {
			int sum = arr[left] + arr[right];

			if (sum == target) {
				result.add(new int[] { arr[left], arr[right] });

				// Skip duplicates on both sides
				while (left < right && arr[left] == arr[left + 1])
					left++;
				while (left < right && arr[right] == arr[right - 1])
					right--;

				left++;
				right--;

			} else if (sum < target) {
				left++;
			} else {
				right--;
			}
		}
		return result;
	}

	public static void main(String[] args) {
	/**	Scanner scanner = new Scanner(System.in);

		System.out.print("Enter sorted array (comma-separated): ");
		String[] tokens = scanner.nextLine().trim().split(",");
		int[] arr = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			arr[i] = Integer.parseInt(tokens[i].trim());
		}

		System.out.print("Enter target: ");
		int target = Integer.parseInt(scanner.nextLine().trim()); */
		
		//int[] arr= {1, 2, 3, 4, 5, 6, 7, 8};
		int[] arr= {1, 1, 2, 2, 5};
		int target=4;
		List<int[]> pairs = findPairs(arr, target);

		System.out.println("\nArray : " + Arrays.toString(arr));
		System.out.println("Target: " + target);
		System.out.println();

		if (pairs.isEmpty()) {
			System.out.println("No pairs found that sum to " + target);
		} else {
			StringBuilder sb = new StringBuilder("Output: ");
			for (int i = 0; i < pairs.size(); i++) {
				sb.append("(").append(pairs.get(i)[0]).append(",").append(pairs.get(i)[1]).append(")");
				if (i < pairs.size() - 1)
					sb.append(", ");
			}
			System.out.println(sb);
		}

//		scanner.close();
	}
}