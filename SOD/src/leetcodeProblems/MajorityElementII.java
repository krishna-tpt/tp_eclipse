package leetcodeProblems;

import java.util.ArrayList;
import java.util.List;

public class MajorityElementII {
	public static void main(String[] args) {
		int[] arr = { 1, 1, 1, 3, 3, 2, 2, 2 };
		List<Integer> list = majorityElement(arr);
		System.out.println(list);
	}

	public static List<Integer> majorityElement(int[] nums) {
		// Pass 1: Find candidates
		int candidate1 = 0, candidate2 = 0;
		int count1 = 0, count2 = 0;

		for (int num : nums) {
			if (num == candidate1) {
				count1++;
			} else if (num == candidate2) {
				count2++;
			} else if (count1 == 0) {
				candidate1 = num;
				count1 = 1;
			} else if (count2 == 0) {
				candidate2 = num;
				count2 = 1;
			} else {
				count1--;
				count2--;
			}
		}

		// Pass 2: Verify candidates
		count1 = 0;
		count2 = 0;
		for (int num : nums) {
			if (num == candidate1)
				count1++;
			else if (num == candidate2)
				count2++;
		}

		List<Integer> result = new ArrayList<>();
		if (count1 > nums.length / 3)
			result.add(candidate1);
		if (count2 > nums.length / 3)
			result.add(candidate2);

		return result;
	}

}
