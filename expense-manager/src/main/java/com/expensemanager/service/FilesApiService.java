package com.expensemanager.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expensemanager.model.WorkDriveFile;
import com.expensemanager.util.AppConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /api/files Returns JSON list of all files in the main WorkDrive folder.
 */
public class FilesApiService {

	private static final Logger log = LoggerFactory.getLogger(FilesApiService.class);

	public boolean UploadFile(File file) throws Exception {
		UploadFileAPI upload = new UploadFileAPI();
		boolean isUploaded = upload.uploadToWorkDrive(file);

		ListFilesService listservice = new ListFilesService();
		List<WorkDriveFile> list = listservice.listFiles();
		if (list.size() > 7) {
			Collections.sort(list);
		}

		return false;
	}
}
