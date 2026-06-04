package tptTest;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Z24_LRUCache {

	private final int capacity;
	private final LinkedHashMap<Integer, Integer> cache;

	public Z24_LRUCache(int capacity) {
		this.capacity = capacity;
		// accessOrder=true: get/put moves entry to end (most recently used)
		this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
				System.out.println();
				return size() > capacity;
			}
		};
	}

	public int get(int key) {
		return cache.getOrDefault(key, -1);
	}

	public void put(int key, int value) {
		cache.put(key, value);
	}

	/** Keys from LRU → MRU */
	public String cacheKeys() {
		StringBuilder sb = new StringBuilder("[");
		Iterator<Integer> it = cache.keySet().iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	/** Full state: key:value pairs */
	public String state() {
		StringBuilder sb = new StringBuilder("[Cache ");
		Iterator<Map.Entry<Integer, Integer>> it = cache.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> e = it.next();
			sb.append(e.getKey()).append(":").append(e.getValue());
			if (it.hasNext())
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter capacity : ");
		int capacity = Integer.parseInt(scanner.nextLine().trim());

		Z24_LRUCache lru = new Z24_LRUCache(capacity);

		while (true) {
			System.out.print("> ");
			if (!scanner.hasNextLine())
				break;
			String line = scanner.nextLine().trim();
			if (line.isEmpty())
				continue;

			String[] parts = line.split("\\s+");
			String command = parts[0].toLowerCase();

			switch (command) {
			case "put":
				if (parts.length != 3) {
					System.out.println("Usage: put <key> <value>");
					break;
				}
				int pKey = Integer.parseInt(parts[1]);
				int pVal = Integer.parseInt(parts[2]);
				lru.put(pKey, pVal);
				System.out.println("OK (Cache : " + lru.cacheKeys() + ")");
				break;

			case "get":
				if (parts.length != 2) {
					System.out.println("Usage: get <key>");
					break;
				}
				int gKey = Integer.parseInt(parts[1]);
				int result = lru.get(gKey);
				System.out.println(result + " (Cache : " + lru.cacheKeys() + ")");
				break;

			case "state":
				System.out.println(lru.state());
				break;

			case "exit":
				System.out.println("Goodbye!");
				scanner.close();
				return;

			default:
				System.out.println("Unknown command: '" + command + "'. Use put, get, state, or exit.");
			}
		}
		scanner.close();
	}
}