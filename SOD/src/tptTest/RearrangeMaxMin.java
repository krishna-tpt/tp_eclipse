package tptTest;

public class RearrangeMaxMin {

	public static void main(String[] args) {
		int[] arr = { 7, 5, 2, 11, 13, 6 };

		quickSort(arr, 0, arr.length - 1);

		rearrange(arr);

		for (int num : arr) {
			System.out.print(num + " ");
		}
	}

	// QuickSort
	static void quickSort(int[] arr, int low, int high) {
		if (low < high) {
			int pi = partition(arr, low, high);

			quickSort(arr, low, pi - 1);
			quickSort(arr, pi + 1, high);
		}
	}

	static int partition(int[] arr, int low, int high) {
		int pivot = arr[high];
		int i = low - 1;

		for (int j = low; j < high; j++) {
			if (arr[j] < pivot) {
				i++;
				swap(arr, i, j);
			}
		}

		swap(arr, i + 1, high);
		return i + 1;
	}

	static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	// Rearranging in-place
	static void rearrange(int[] arr) {

		int n = arr.length;
		int maxIdx = n - 1;
		int minIdx = 0;

		int maxElem = arr[n - 1] + 1;

		for (int i = 0; i < n; i++) {

			if (i % 2 == 0) {
				arr[i] = arr[i] + (arr[maxIdx] % maxElem) * maxElem;
				maxIdx--;
			} else {
				arr[i] = arr[i] + (arr[minIdx] % maxElem) * maxElem;
				minIdx++;
			}
		}

		for (int i = 0; i < n; i++) {
			arr[i] = arr[i] / maxElem;
		}
	}
}