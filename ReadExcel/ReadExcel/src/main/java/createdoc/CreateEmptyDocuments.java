package createdoc;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateEmptyDocuments {
	public static void main(String[] args) {

		// Output folder
		String outputFolder = "/home/dev021/Zoho WorkDrive (firstportal01)/My Folders/TestProject/";

		File file = new File(outputFolder);
		if (!file.exists()) {
			System.out.println("Folder create error: ");
			return;
		}

		// 1000 documents create பண்ணுங்கள்
		for (int i = 1001; i <= 2000; i++) {
			String fileName = outputFolder + "Document_" + i + ".docx";

			try (XWPFDocument document = new XWPFDocument(); FileOutputStream out = new FileOutputStream(fileName)) {

				document.write(out);
				System.out.println("Created: " + fileName);

			} catch (IOException e) {
				System.out.println("Error creating " + fileName + ": " + e.getMessage());
			}
		}

		System.out.println("✅ 1000 documents created successfully!");
	}
}