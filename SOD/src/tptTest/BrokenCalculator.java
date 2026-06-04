package tptTest;

public class BrokenCalculator {
	public static void main(String[] args) {
		int startValue = 3;
		int target = 11;

		System.out.println(findCount(startValue, target));
	}

	private static int findCount(int startValue, int target) {

		int ops = 0;

		// Work backwards from target until it is <= startValue
		while (startValue < target) {
			if (target % 2 == 0) {
				target /= 2;
			} else {
				target += 1;
			}
			ops++;
		}

		// Now if startValue >= target, we just need (startValue - target) subtractions
		ops += startValue - target;
		return ops;
	}

}
