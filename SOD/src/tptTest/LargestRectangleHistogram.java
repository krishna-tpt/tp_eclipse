package tptTest;

import java.util.Stack;

public class LargestRectangleHistogram {
	public int largestRectangleArea(int[] heights) {
		if (heights == null || heights.length == 0)
			return 0;

		Stack<Integer> st = new Stack<Integer>(); // holds indices, heights increasing
		int max = 0;
		int n = heights.length;

		for (int i = 0; i <= n; i++) {
			int curr = (i == n) ? 0 : heights[i]; // sentinel 0 at the end

			// Ensure stack maintains increasing heights
			while (!st.isEmpty() && curr < heights[st.peek()]) {
				int h = heights[st.pop()];
				int leftIndex = st.isEmpty() ? 0 : st.peek() + 1;
				int width = i - leftIndex; // spans leftIndex .. i-1
				max = Math.max(max, h * width);
			}
			st.push(i);
		}
		return max;
	}

	// quick demo
	public static void main(String[] args) {
		LargestRectangleHistogram sol = new LargestRectangleHistogram();
		System.out.println(sol.largestRectangleArea(new int[] { 2, 1, 5, 6, 2, 3 })); // 10
		System.out.println(sol.largestRectangleArea(new int[] { 2, 4 })); // 4
		System.out.println(sol.largestRectangleArea(new int[] { 1, 1 })); // 2
	}
}
