package tptTest;

public class MaximumSumTwoNonOverlappingSubarrays {

	public static void main(String[] args) {

		int[] nums = { 0, 6, 5, 2, 2, 5, 1, 9, 4 };
		int firstLen = 1;
		int secondLen = 2;

		int result = maxSumTwoNoOverlap(nums, firstLen, secondLen);
		System.out.println("Maximum Sum = " + result); // Expected: 20
	}

	public static int maxSumTwoNoOverlap(int[] nums, int firstLen, int secondLen) {
		return Math.max(maxSum(nums, firstLen, secondLen), maxSum(nums, secondLen, firstLen));
	}

	private static int maxSum(int[] nums, int len1, int len2) {

		int n = nums.length;

		// Prefix sum
		int[] prefix = new int[n + 1];
		for (int i = 0; i < n; i++) {
			prefix[i + 1] = prefix[i] + nums[i];
		}

		int maxL = 0;
		int result = 0;

		// i = ending index of M-length subarray
		for (int i = len1 + len2; i <= n; i++) {

			// best L-length subarray before M
			maxL = Math.max(maxL, prefix[i - len2] - prefix[i - len2 - len1]);

			// current M-length subarray
			int currM = prefix[i] - prefix[i - len2];

			result = Math.max(result, maxL + currM);
		}

		return result;
	}
}
