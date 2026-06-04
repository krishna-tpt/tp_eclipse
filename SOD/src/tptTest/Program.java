package tptTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Program {
	public static void main(String[] args) {
		int[] arr= {-8, 2, 3, -6, 10};
		int k=2;
		List<Integer> list=firstNegativeInWindow(arr,k);
		System.out.println(list);
		
	}
	
	public static List<Integer> firstNegativeInWindow(int[] arr, int k) {
	    List<Integer> result = new ArrayList<>();
	    Queue<Integer> negatives = new LinkedList<>();

	    for (int i = 0; i < arr.length; i++) {
	        // Add negative numbers to queue
	        if (arr[i] < 0) {
	            negatives.offer(arr[i]);
	        }

	        // When window size is reached
	        if (i >= k - 1) {
	            // First negative or 0
	            result.add(negatives.isEmpty() ? 0 : negatives.peek());

	            // Remove element going out of window
	            if (!negatives.isEmpty() && negatives.peek() == arr[i - k + 1]) {
	                negatives.poll();
	            }
	        }
	    }

	    return result;
	}

}
