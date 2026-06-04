package tptTest;

public class Z24_ProfitLoss {
	public static void main(String[] args) {

		int[] arr = { 10, 20,30,35,12};

		int minPrice = arr[0];
		int maxPrice = arr[0];

		int maxProfit = Integer.MIN_VALUE;
		int maxLoss = Integer.MAX_VALUE;

		for (int i = 1; i < arr.length; i++) {

			// Calculate profit
			maxProfit = Math.max(maxProfit, arr[i] - minPrice);
			minPrice = Math.min(minPrice, arr[i]);

			// Calculate loss
			maxLoss = Math.min(maxLoss, arr[i] - maxPrice);
			maxPrice = Math.max(maxPrice, arr[i]);
		}

		System.out.println("Max Profit: " + maxProfit);
		System.out.println("Max Loss: " + maxLoss);
	}
}