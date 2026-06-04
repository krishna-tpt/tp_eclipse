package tptTest;

import java.util.HashMap;

public class SubarraySumEqualsK {
	public static void main(String[] args) {
		int[] nums = { 1,2,3};
		int k = 3;

		System.out.println(subarraySum(nums, k));
	}

	public static int subarraySum(int[] nums, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        
        // prefix sum 0 one time appears (for subarrays starting at index 0)
        map.put(0, 1);

        int prefix = 0;
        int count = 0;

        for (int num : nums) {
            prefix += num;

            // check if prefix - k exists in map
            if (map.containsKey(prefix - k)) {
                count += map.get(prefix - k);
            }

            // update map with current prefix sum
            map.put(prefix, map.getOrDefault(prefix, 0) + 1);
        }
        
        return count;
    }

}
