package tptTest;

public class MiniRegexFinder {

	public static String findFirstMatch(String text, String pattern) {
		for (int i = 0; i < text.length(); i++) {
			String match = tryMatch(text, pattern, i);
			if (match != null) {
				return match;
			}
		}
		return "No match found";
	}

	private static String tryMatch(String text, String pattern, int start) {
		int text_idx = start, pattern_idx = 0;
		StringBuilder sb = new StringBuilder();

		while (pattern_idx < pattern.length()) {
			if (text_idx >= text.length())
				return null;

			char pc = pattern.charAt(pattern_idx);

			// Check if next char in pattern is * or +
			if (pattern_idx + 1 < pattern.length() && (pattern.charAt(pattern_idx + 1) == '*' 
					|| pattern.charAt(pattern_idx + 1) == '+')) {
				char op = pattern.charAt(pattern_idx + 1);

				if (op == '+' && ((text_idx >= text.length() || text.charAt(text_idx) != pc)))
					return null; //must match at least once
				
				while (text_idx < text.length() && text.charAt(text_idx) == pc) {
					sb.append(text.charAt(text_idx));
					text_idx++;
				}
				pattern_idx += 2; // skip char and '+'

			} else {
				// exact match
				if (text.charAt(text_idx) != pc)
					return null;
				sb.append(text.charAt(text_idx));
				text_idx++;
				pattern_idx++;
			}
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		String text1 = "abcbbcabb"; // abcbbcabb
		String pattern1 = "cb*cab+"; // cb*cab+
		System.out.println(findFirstMatch(text1, pattern1)); // cbbcabb

		String text2 = "abcfbbbacbk"; // abcfbbbacbk
		String pattern2 = "bbk*ac+"; // bbk*ac+
		System.out.println(findFirstMatch(text2, pattern2)); // bbac
	}
}
