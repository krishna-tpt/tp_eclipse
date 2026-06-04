package tptTest;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringWith_K_UniqueCharacters {

	public static void main(String[] args) {
		String s = "aabacbebebe";
		int k = 3;
		System.out.println(longestKSubstr(s, k)); // 7
	}

	public static int longestKSubstr(String s, int k) {

		int left = 0, maxLen = -1;
		Map<Character, Integer> freq = new HashMap<>();

		for (int right = 0; right < s.length(); right++) {

			char c = s.charAt(right);
			freq.put(c, freq.getOrDefault(c, 0) + 1);

			// shrink window if unique chars exceed k
			while (freq.size() > k) {
				char leftChar = s.charAt(left);
				freq.put(leftChar, freq.get(leftChar) - 1);

				if (freq.get(leftChar) == 0)
					freq.remove(leftChar);

				left++;
			}

			// update result when exactly k unique chars
			if (freq.size() == k) {
				maxLen = Math.max(maxLen, right - left + 1);
			}
		}

		return maxLen;
	}
}
