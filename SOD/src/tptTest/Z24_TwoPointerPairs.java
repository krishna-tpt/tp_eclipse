package tptTest;

public class Z24_TwoPointerPairs {

	public static void main(String[] args) {
		int[] arr = { 1,1,2,2,3,5 };
		int target = 7;
		findPairs(arr, target);
	}

	private static void findPairs(int[] arr, int target) {

		int right = arr.length - 1;
		int sum = 0;
		for (int left = 0; left < arr.length; left++) {
			sum = arr[left] + arr[right];

			if (sum == target) {
				System.out.println("(" + arr[left] + "," + arr[right] + ")");
				
				while(left< right && arr[left]==arr[left+1]) {
					left++;
					continue;
				}
				
				while(left< right && arr[right]==arr[right-1]) {
					left--;
					continue;
				}
				
				left++;
				right--;
			}else if(sum>target) {
				right--;
			}else
				left++;
			
			
	}

}
}
