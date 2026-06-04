package tptTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileReadTest {
	public static void main(String[] args) {
		int count=0;
		String filename="/home/dev021/Downloads/SMS Autria/2026-02-06_0.log";
		File file =new File(filename);
		
		try(BufferedReader br =new BufferedReader(new FileReader(file))){
			String line;
			while((line=br.readLine())!=null) {
				if (line.contains("Failed save MCost record")) {
					count+=1;
				}
			}
			
			System.out.println("Count : "+count);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
