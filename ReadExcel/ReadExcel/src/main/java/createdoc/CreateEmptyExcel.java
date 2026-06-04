package createdoc;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateEmptyExcel {

	public static void main(String[] args) {
		String outputFolder = "/home/dev021/Zoho WorkDrive (firstportal01)/My Folders/TestProject/";

		File file = new File(outputFolder);
		if (!file.exists()) {
			System.out.println("Folder create error: ");
			return;
		}

		// 1000 Excel files create பண்ணுங்கள்
		for (int i = 1001; i <= 2000; i++) {
			String fileName = outputFolder + "Excel_" + i + ".xlsx";

			try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(fileName)) {

				// Empty sheet add பண்ணுங்கள்
				workbook.createSheet("Sheet1");
				workbook.write(out);
				System.out.println("Created: " + fileName);

			} catch (IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}

		System.out.println("✅ 1000 Excel files created successfully!");
	}
}