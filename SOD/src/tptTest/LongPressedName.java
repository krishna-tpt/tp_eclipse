package tptTest;

public class LongPressedName {
	public static void main(String[] args) {
		LongPressedName lp = new LongPressedName();
		String name = "alex";
		String typed = "aaleex";
		System.out.println(lp.isLongPressedName(name, typed));

	}

	public boolean isLongPressedName(String name, String typed) {

		int nameLength = name.length();
		int typedLength = typed.length();

		int i = 0, j = 0;

		while (j < typedLength) {
			if (i < nameLength && name.charAt(i) == typed.charAt(j)) {
				i++;
				j++;
			} else if (j > 0 && typed.charAt(j) == typed.charAt(j- 1))
				j++;
			else
				return false;

		}
		return i == nameLength;
	}

}
