package tptTest;

import java.util.*;

class HuffmanNode implements Comparable<HuffmanNode> {
	char ch; // character (for leaf nodes)
	int freq; // frequency
	HuffmanNode left, right;

	HuffmanNode(char ch, int freq) {
		this.ch = ch;
		this.freq = freq;
	}

	HuffmanNode(int freq, HuffmanNode l, HuffmanNode r) {
		this.ch = '\0';
		this.freq = freq;
		this.left = l;
		this.right = r;
	}

	// for PriorityQueue ordering
	@Override
	public int compareTo(HuffmanNode other) {
		return this.freq - other.freq;
	}

	// leaf check
	public boolean isLeaf() {
		return left == null && right == null;
	}
}

public class HuffmanEncodeDecode {

	// Build frequency map
	private static Map<Character, Integer> buildFreqMap(String text) {
		Map<Character, Integer> freq = new HashMap<>();
		for (char c : text.toCharArray()) {
			freq.put(c, freq.getOrDefault(c, 0) + 1);
		}
		return freq;
	}

	// Build Huffman tree
	private static HuffmanNode buildTree(Map<Character, Integer> freqMap) {
		PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
		for (var e : freqMap.entrySet()) {
			pq.add(new HuffmanNode(e.getKey(), e.getValue()));
		}

		// edge case: only one unique character
		if (pq.size() == 1) {
			// create a dummy node so root won't be leaf (makes encoding/decoding
			// consistent)
			HuffmanNode only = pq.poll();
			pq.add(new HuffmanNode(only.freq, only, null));
		}

		while (pq.size() > 1) {
			HuffmanNode a = pq.poll();
			HuffmanNode b = pq.poll();
			HuffmanNode parent = new HuffmanNode(a.freq + b.freq, a, b);
			pq.add(parent);
		}
		return pq.poll();
	}

	// Generate codes by traversing the tree
	private static void generateCodes(HuffmanNode root, String prefix, Map<Character, String> codeMap) {
		if (root == null)
			return;
		if (root.isLeaf()) {
			// if prefix empty (single char case), assign "0"
			codeMap.put(root.ch, prefix.length() > 0 ? prefix : "0");
			return;
		}
		generateCodes(root.left, prefix + "0", codeMap);
		generateCodes(root.right, prefix + "1", codeMap);
	}

	// Encode text using code map
	private static String encode(String text, Map<Character, String> codeMap) {
		StringBuilder sb = new StringBuilder();
		for (char c : text.toCharArray()) {
			sb.append(codeMap.get(c));
		}
		return sb.toString();
	}

	// Decode bit string using Huffman tree
	private static String decode(String bitString, HuffmanNode root) {
		StringBuilder result = new StringBuilder();
		HuffmanNode node = root;
		for (int i = 0; i < bitString.length(); i++) {
			char bit = bitString.charAt(i);
			node = (bit == '0') ? node.left : node.right;
			if (node.isLeaf()) {
				result.append(node.ch);
				node = root; // go back to root for next symbol
			}
		}
		return result.toString();
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter text to encode:");
		String text = sc.nextLine();

		if (text == null || text.length() == 0) {
			System.out.println("Empty input. Exiting.");
			return;
		}

		// 1. Frequency map
		Map<Character, Integer> freqMap = buildFreqMap(text);
		System.out.println("\nFrequencies:");
		for (var e : freqMap.entrySet()) {
			System.out.println("'" + e.getKey() + "' : " + e.getValue());
		}

		// 2. Build Huffman Tree
		HuffmanNode root = buildTree(freqMap);

		// 3. Generate codes
		Map<Character, String> codeMap = new HashMap<>();
		generateCodes(root, "", codeMap);
		System.out.println("\nHuffman Codes:");
		for (var e : codeMap.entrySet()) {
			System.out.println("'" + e.getKey() + "' -> " + e.getValue());
		}

		// 4. Encode
		String encoded = encode(text, codeMap);
		System.out.println("\nEncoded bit string:");
		System.out.println(encoded);

		// 5. Decode
		String decoded = decode(encoded, root);
		System.out.println("\nDecoded text:");
		System.out.println(decoded);

		// 6. Compression info
		int originalBits = text.length() * 8; // assuming ASCII/1 byte per char
		int compressedBits = encoded.length();
		System.out.println("\nCompression:");
		System.out.println("Original size (bits, assuming 8 bits/char): " + originalBits);
		System.out.println("Compressed size (bits): " + compressedBits);
		double ratio = (double) compressedBits / originalBits;
		System.out.printf("Compression ratio: %.3f (compressed/original)%n", ratio);
		sc.close();
	}
}
