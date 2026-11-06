package com.test.sod;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String token = null;
    	token = " 20";
    	int counter =135;
    	token = lpad(String.valueOf(counter), 7, '0');
    	System.out.println("output :"+token);
    }
    
    
    public static String lpad(String string, int length, char padChar) {
		if (string.length() >= length)
			return string.substring(0, length);

		StringBuffer paddedString = new StringBuffer();
		if (string.length() < length) {
			for (int i = 0; i < length - string.length(); i++)
				paddedString.append(padChar);
		}
		paddedString.append(string);
		return paddedString.toString();
	}
}
